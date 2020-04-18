package com.devfactory.processautomation.qa.rwa.service;

import com.devfactory.processautomation.cqa.CqaQeClient;
import com.devfactory.processautomation.cqa.dto.NewHireRequest;
import com.devfactory.processautomation.cqa.dto.NewHireResponse;
import com.devfactory.processautomation.devextreme.LoadOptions;
import com.devfactory.processautomation.devextreme.QueryBuilder;
import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.qa.rwa.domain.Assessment;
import com.devfactory.processautomation.qa.rwa.domain.Region;
import com.devfactory.processautomation.qa.rwa.infrastructure.RunDeckConfig;
import com.devfactory.processautomation.qa.rwa.infrastructure.WebConfig;
import com.devfactory.processautomation.qa.rwa.repository.AssessmentRepository;
import com.devfactory.processautomation.qa.rwa.repository.CountryRegionRepository;
import com.devfactory.processautomation.qa.rwa.repository.CountryRepository;
import com.devfactory.processautomation.qa.rwa.repository.RegionRepository;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentRequest;
import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.Candidate;
import com.devfactory.processautomation.qa.rwa.service.dtos.DependentAppError;
import com.devfactory.processautomation.qa.rwa.service.dtos.InvalidRequestError;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.KillResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.PagedResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.RegionDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.RunDeckOptions;
import com.devfactory.processautomation.rundeck.RunDeckClient;
import com.devfactory.processautomation.rundeck.dto.ExecutionInfo;
import com.devfactory.processautomation.rundeck.dto.JobInfo;
import com.devfactory.processautomation.util.StringUtils;
import com.devfactory.processautomation.validator.ValidationError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
class AssessmentServiceImpl extends BaseService implements AssessmentService {
    private static final String FORMAT_ASSESSMENT_URL = "%s/assessments/%s";
    private static final String STATUS_RUNNING = "running";
    private static final String STATUS_FAILED = "failed";
    private static final String STATUS_SUCCEEDED = "succeeded";
    private static final String STATUS_SCHEDULED = "scheduled";
    private static final String STATUS_INACTIVE = "inactive";
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_IN_PROGRESS = "in progress";
    private static final String STATUS_TIMEDOUT = "timedout";
    private static final String ACCOUNT_ID_PREFIX = "mtqa.";
    private static final String AGENT = "RWA-Automation";
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String DEFAULT_REGION = "us-east-1";
    private static final Pattern CALLBACK_URL =
            Pattern.compile("https?://.+/services/apexrest/assessments/v1/(.+)/(.+)");
    private static final int GROUP_ORDER_ID = 2;
    private static final int GROUP_VENDOR_ID = 1;
    private static final int MAX_LENGTH = 9;

    private final @NonNull AssessmentRepository repository;
    private final @NonNull RunDeckClient runDeckClient;
    private final @NonNull CqaQeClient qeClient;
    private final @NonNull RunDeckConfig runDeckConfig;
    private final @NonNull WebConfig webConfig;
    private final @NonNull Logger logger;

    private final @NonNull CountryRepository countryRepository;
    private final @NonNull CountryRegionRepository countryRegionRepository;
    private final @NonNull RegionRepository regionRepository;
    private Region defaultRegion;

    public AssessmentServiceImpl(EntityManager entityManager, QueryBuilder queryBuilder, Logger logger,
            WebConfig webConfig, RunDeckClient runDeckClient, AssessmentRepository repository,
            CountryRepository countryRepository, RegionRepository regionRepository,
            CountryRegionRepository countryRegionRepository, CqaQeClient qeClient, RunDeckConfig runDeckConfig) {
        super(entityManager, queryBuilder, logger);
        this.webConfig = webConfig;
        this.logger = logger;
        this.runDeckClient = runDeckClient;
        this.repository = repository;
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.countryRegionRepository = countryRegionRepository;
        this.qeClient = qeClient;
        this.runDeckConfig = runDeckConfig;
    }

    @Override
    public AssessmentResponse createAssessment(AssessmentRequest request) {
        AssessmentRequest validatedRequest = validate(request);
        if (!validatedRequest.isValid()) {
            return errorResponse(validatedRequest.getErrors());
        }
        Optional<Assessment> existingAssessment = getExistingAssessment(validatedRequest);
        if(existingAssessment.isPresent()) {
            return prepareResponse(existingAssessment.get());
        }
        Candidate candidate = request.getCandidate();
        Region region = getRegion(candidate.getCountry());
        ExecutionInfo executionInfo = runProvisioningJob(candidate, region);
        if (!executionInfo.isValid()) {
            logger.error("Failed to start provision job for user : {}. Reason: {}",
                    request.getCandidate().getFirstName(),
                    executionInfo.getErrors().get(0).getError().getMessage());
            return errorResponse(Collections.singletonList(
                    new DependentAppError("RUNDECK", "Failed to provision workspace", executionInfo.getErrors())));
        }
        return prepareResponse(createAssessment(validatedRequest, executionInfo, region));
    }

    @Override
    public List<ValidationError> updateAssessments() {
        List<ValidationError> result = new ArrayList<>();
        ItemListResponse<Assessment> refreshedList = refreshProvisioningStatus();
        result.addAll(refreshedList.getErrors());
        List<Assessment> modifiedAssessments = refreshedList.getItems();
        result.addAll(onboardToQeTool(getSuccessfulAssessments(modifiedAssessments)));
        result.addAll(retryFailedExecutions(getFailedAssessments(modifiedAssessments)));
        repository.saveAll(modifiedAssessments);
        return result;
    }

    @Override
    public ItemListResponse<AssessmentDto> getAllAssessments() {
        ItemListResponse<AssessmentDto> result = new ItemListResponse<>();
        result.setItems(repository.findAll().stream().map(assessment -> toAssessmentDto(assessment))
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public AssessmentDto updateStatus(String uuid, String status) {
        Optional<Assessment> assessment = repository.findByUuid(uuid);
        if (!assessment.isPresent()) {
            return invalidUuidError(uuid);
        }
        assessment.get().setStatus(status);
        return toAssessmentDto(repository.save(assessment.get()));
    }

    @Override
    public AssessmentDto retryFailedProvisioning(String uuid) {
        Optional<Assessment> assessment = repository.findByUuid(uuid);
        if (!assessment.isPresent()) {
            return invalidUuidError(uuid);
        }
        List<ValidationError> errors = retryFailedExecutions(Collections.singletonList(assessment.get()));
        if (errors.isEmpty()) {
            return toAssessmentDto(repository.save(assessment.get()));
        }
        AssessmentDto response = AssessmentDto.builder().build();
        response.addError(new DependentAppError("RUNDECK", "Retry failed for assessment id=" + uuid, errors));
        return response;
    }

    @Override
    public AssessmentDto kill(String uuid) {
        Optional<Assessment> assessment = repository.findByUuid(uuid);
        if (!assessment.isPresent()) {
            return invalidUuidError(uuid);
        }
        return kill(assessment.get());
    }

    @Override
    public KillResponse killByEmail(String emailId) {
        if (StringUtils.isBlank(emailId)) {
            return new KillResponse("Email id should not be empty");
        }
        List<Assessment> assessments = repository.findByEmail(emailId);
        List<AssessmentDto> results =
                assessments.stream().map(assessment -> kill(assessment)).collect(Collectors.toList());
        return new KillResponse(results);
    }

    @Override
    public PagedResponse<AssessmentDto> getAllAssessments(LoadOptions options) {
        PagedResponse<AssessmentDto> result = new PagedResponse<>();
        PagedResponse<Assessment> entities = loadEntities(options, Assessment.class);
        if (!entities.isValid()) {
            result.addErrors(entities.getErrors());
            return result;
        }
        result.setItems(toAssessmentDto(entities.getItems()));
        result.setTotalElements(entities.getTotalElements());
        return result;
    }

    private Optional<Assessment> getExistingAssessment(AssessmentRequest request) {
        Optional<String> orderId = getSalesforceOrderId(request.getCallbackUrl());
        if (!orderId.isPresent()) {
            return Optional.empty();
        }
        List<Assessment> assessments = repository.findByOrderId(orderId.get());
        return assessments == null || assessments.isEmpty() ? Optional.empty() : Optional.of(assessments.get(0));
    }
    
    private AssessmentDto kill(Assessment assessment) {
        AssessmentDto result = AssessmentDto.builder().build();
        if (STATUS_RUNNING.equals(assessment.getProvisioningStatus())) {
            result.addError(new InvalidRequestError("Provisioning in progress. Cannot deprovision"));
            return result;
        }
        ExecutionInfo response = deProvision(assessment);
        if (!response.isValid()) {
            result.addError(new DependentAppError("RUNDECK", "Deprovision failed", response.getErrors()));
            return result;
        }
        return toAssessmentDto(repository.save(setDeProvisioningInfo(assessment, response)));

    }

    private List<Assessment> getFailedAssessments(List<Assessment> assessments) {
        return getByStatus(new HashSet<>(Arrays.asList(STATUS_FAILED, STATUS_TIMEDOUT)), assessments);
    }

    private List<Assessment> getSuccessfulAssessments(List<Assessment> assessments) {
        return getByStatus(Collections.singleton(STATUS_SUCCEEDED), assessments);
    }

    private AssessmentDto invalidUuidError(String uuid) {
        AssessmentDto response = AssessmentDto.builder().build();
        response.addError(new InvalidRequestError("Invalid assessment id=" + uuid));
        return response;
    }

    private List<AssessmentDto> toAssessmentDto(List<Assessment> assessments) {
        return assessments.stream().map(assessment -> toAssessmentDto(assessment)).collect(Collectors.toList());
    }

    private AssessmentDto toAssessmentDto(Assessment assessment) {
        return AssessmentDto.builder()
                .uuid(assessment.getUuid())
                .candidateName(assessment.getCandidateName())
                .rwaStatus(assessment.getRwaStatus())
                .status(assessment.getStatus())
                .created(assessment.getCreated())
                .candidateEmail(assessment.getCandidateEmail())
                .provisioningJobId(String.valueOf(assessment.getProvisioningJobId()))
                .provisionJobLink(runDeckConfig.getRunDeckExecutionUrl() + assessment.getProvisioningJobId())
                .manualRetryEnabled(assessment.getProvisioningAttempts() >= runDeckConfig.getMaxProvisioningAttempts())
                .salesforceOrderId(assessment.getSalesforceOrderId())
                .username(assessment.getUsername())
                .provisioningRegion(assessment.getProvisioningRegion() == null ? null
                        : new RegionDto(assessment.getProvisioningRegion().getName()))
                .build();
    }

    private static Optional<String> getSalesforceOrderId(String callBackUrl) {
        return parseCallbackUrl(callBackUrl, GROUP_ORDER_ID);
    }

    private static Optional<String> getVendorId(String callBackUrl) {
        return parseCallbackUrl(callBackUrl, GROUP_VENDOR_ID);
    }

    private static Optional<String> parseCallbackUrl(String callBackUrl, int group) {
        Matcher matcher = CALLBACK_URL.matcher(callBackUrl);
        return matcher.matches() ? Optional.of(matcher.group(group)) : Optional.empty();
    }

    private String getRWAStatus(Assessment assessment) {
        if (assessment.getDeprovisionJobId() != null) {
            return STATUS_INACTIVE;
        }
        if (STATUS_RUNNING.equals(assessment.getProvisioningStatus())) {
            return STATUS_IN_PROGRESS;
        }
        if (STATUS_SUCCEEDED.equals(assessment.getProvisioningStatus())) {
            return STATUS_ACTIVE;
        }
        return STATUS_FAILED;
    }

    private Assessment createAssessment(AssessmentRequest request, ExecutionInfo executionInfo, Region region) {
        Assessment assessment = createNewAssessment(request);
        assessment = setProvisioningInfo(assessment, executionInfo, region);
        return repository.save(assessment);
    }

    private AssessmentResponse errorResponse(List<ValidationError> errors) {
        AssessmentResponse errorResponse = new AssessmentResponse();
        errorResponse.addErrors(errors);
        return errorResponse;
    }

    private List<Assessment> getByStatus(Set<String> statuses, List<Assessment> assessments) {
        return assessments.stream().filter(assessment -> statuses.contains(assessment.getProvisioningStatus()))
                .collect(Collectors.toList());
    }

    private ItemListResponse<Assessment> refreshProvisioningStatus() {
        ItemListResponse<Assessment> result = new ItemListResponse<>();
        List<Assessment> updatedAssessments = new ArrayList<>();
        for (Assessment assessment : repository.findByStatus(Arrays.asList(STATUS_RUNNING))) {
            ExecutionInfo response = runDeckClient.getExecutionInfo(assessment.getProvisioningJobId());
            if (!response.isValid()) {
                logger.error("Could not get status of execution {}. Reason - {}", assessment.getProvisioningJobId(),
                        response.getErrors().get(0).getError().getMessage());
                result.addErrors(response.getErrors());
                continue;
            }
            String status = response.getStatus();
            assessment.setProvisioningStatus(status);
            assessment.setRwaStatus(getRWAStatus(assessment));
            updatedAssessments.add(assessment);
        }
        result.setItems(updatedAssessments);
        return result;
    }

    private List<ValidationError> onboardToQeTool(List<Assessment> assessments) {
        List<ValidationError> result = new ArrayList<>();
        for (Assessment assessment : assessments) {
            NewHireResponse response = qeClient.addNewHire(getXoHireRequest(assessment));
            if (!response.isValid()) {
                logger.error("Could not onboard candidate to QE tool for assessment = {}. Reason = {}",
                        assessment.getUuid(), response.getErrors().get(0).getError().getMessage());
                result.addAll(response.getErrors());
                continue;
            }
            assessment.setOnboardedQeTool(true);
        }
        return result;
    }

    private NewHireRequest getXoHireRequest(Assessment assessment) {
        String accoundId = ACCOUNT_ID_PREFIX + assessment.getUsername();
        return NewHireRequest.builder().accountId(accoundId).isActive(true).name(assessment.getCandidateName())
                .xoAgent(AGENT).build();
    }

    private List<ValidationError> retryFailedExecutions(List<Assessment> assessments) {
        List<ValidationError> result = new ArrayList<>();
        for (Assessment assessment : assessments) {
            if (runDeckConfig.isLimitRetries()
                    && assessment.getProvisioningAttempts() >= runDeckConfig.getMaxProvisioningAttempts()) {
                continue;
            }
            deProvision(assessment);
            resetUsername(assessment);
            Candidate candidateDetails = getCandidateDetails(assessment);
            Region region = getRegion(candidateDetails.getCountry());
            ExecutionInfo response = runProvisioningJob(candidateDetails, region);
            if (!response.isValid()) {
                logger.error("Could not retry failed execution {}. Reason - {}", assessment.getProvisioningJobId(),
                        response.getErrors().get(0).getError().getMessage());
                result.addAll(response.getErrors());
                continue;
            }
            setProvisioningInfo(assessment, response, region);
        }
        return result;
    }

    private Candidate getCandidateDetails(Assessment assessment) {
        Candidate candidate = new Candidate();
        candidate.setEmail(assessment.getCandidateEmail());
        candidate.setUsername(assessment.getUsername());
        candidate.setFirstName(assessment.getCandidateFirstName());
        candidate.setLastName(assessment.getCandidateLastName());
        return candidate;
    }

    private ExecutionInfo deProvision(Assessment assessment) {
        ExecutionInfo cleanupResult = runDeprovisionJob(assessment.getUsername());
        if (!cleanupResult.isValid()) {
            logger.error("Deprovision failed for assessment id {}. Reason - {}", assessment.getUuid(),
                    cleanupResult.getErrors().get(0).getError().getMessage());
        }
        return cleanupResult;
    }

    private Assessment resetUsername(Assessment assessment) {
        assessment.setUsername(getUsername(assessment.getCandidateFirstName()));
        return assessment;
    }

    private AssessmentResponse prepareResponse(Assessment assessment) {
        return new AssessmentResponse(
                String.format(FORMAT_ASSESSMENT_URL, webConfig.getFrontendUrl(), assessment.getUuid()));
    }

    private Assessment createNewAssessment(AssessmentRequest request) {
        Assessment assessment = new Assessment();
        assessment.setRequestJson(new Gson().toJson(request));
        assessment = setRequestParams(assessment, request);
        assessment.setStatus(STATUS_SCHEDULED);
        return assessment;
    }

    private Assessment setRequestParams(Assessment assessment, AssessmentRequest request) {
        Candidate candidate = request.getCandidate();
        assessment.setCandidateFirstName(candidate.getFirstName());
        assessment.setCandidateLastName(candidate.getLastName());
        assessment.setCandidateName(String.join(SPACE, candidate.getFirstName(), candidate.getLastName()));
        assessment.setCandidateEmail(candidate.getEmail());
        if (StringUtils.isNotBlank(candidate.getCountry())) {
            countryRepository.findByCode(candidate.getCountry()).ifPresent(c -> assessment.setCandidateCountry(c));
        }
        assessment.setSalesforceOrderId(getSalesforceOrderId(request.getCallbackUrl()).orElse(EMPTY));
        assessment.setVendorId(getVendorId(request.getCallbackUrl()).orElse(EMPTY));
        assessment.setUsername(candidate.getUsername());
        assessment.setTestId(request.getTestId());
        return assessment;
    }

    private AssessmentRequest validate(AssessmentRequest request) {
        if (StringUtils.isBlank(request.getTestId())) {
            logger.error("Test ID is missing");
            request.addError(new InvalidRequestError("Test ID is missing"));
            return request;
        }
        Candidate candidate = request.getCandidate();
        if (candidate == null) {
            logger.error("Candidate details are missing in test ID {} ", request.getTestId());
            request.addError(new InvalidRequestError("Candidate details are missing"));
            return request;
        }
        if (StringUtils.isBlank(candidate.getFirstName())) {
            logger.error("Candidate first name is missing in test ID {} ", request.getTestId());
            request.addError(new InvalidRequestError("Candidate first name is missing"));
            return request;
        }
        if (StringUtils.isBlank(candidate.getLastName())) {
            logger.error("Candidate last name is missing in test ID {} ", request.getTestId());
            request.addError(new InvalidRequestError("Candidate last name is missing"));
            return request;
        }
        if (StringUtils.isBlank(candidate.getEmail())) {
            logger.error("Candidate email is missing in test ID {} ", request.getTestId());
            request.addError(new InvalidRequestError("Candidate email is missing"));
            return request;
        }
        if (StringUtils.isBlank(request.getCallbackUrl())) {
            logger.error("Callback url is missing in test ID {} ", request.getTestId());
            request.addError(new InvalidRequestError("Callback url is missing"));
            return request;
        }
        if (!getSalesforceOrderId(request.getCallbackUrl()).isPresent()) {
            logger.error("Order id is missing in test ID {} ", request.getTestId());
            request.addError(new InvalidRequestError("Order id is missing"));
            return request;
        }
        String country = candidate.getCountry();
        if (StringUtils.isNotBlank(country) && !countryRepository.findByCode(country).isPresent()) {
            logger.error("Invalid country code = {}", country);
            request.addError(new InvalidRequestError("Invalid country code = " + country));
            return request;
        }
        if (StringUtils.isBlank(candidate.getUsername())) {
            candidate.setUsername(getUsername(candidate.getFirstName()));
        }
        candidate.setFirstName(clean(candidate.getFirstName()));
        candidate.setLastName(clean(candidate.getLastName()));
        return request;
    }

    private String getUsername(String name) {
        return trimUsername(clean(name)) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    }

    private Assessment setProvisioningInfo(Assessment assessment, ExecutionInfo executionInfo,
            Region provisionedRegion) {
        if (!executionInfo.isValid()) {
            return assessment;
        }
        logger.debug("Run deck execution id : {} for assessment id : {}", executionInfo.getId(),
                assessment.getUuid());
        assessment.setProvisioningJobId(executionInfo.getId());
        assessment.setProvisioningStatus(executionInfo.getStatus());
        assessment.setProvisioningAttempts(assessment.getProvisioningAttempts() + 1);
        assessment.setRwaStatus(getRWAStatus(assessment));
        assessment.setProvisioningRegion(provisionedRegion);
        return assessment;
    }

    private Assessment setDeProvisioningInfo(Assessment assessment, ExecutionInfo executionInfo) {
        if (!executionInfo.isValid()) {
            return assessment;
        }
        logger.debug("Deprovisioning execution id : {} for assessment id : {}", executionInfo.getId(),
                assessment.getUuid());
        assessment.setDeprovisionJobId(String.valueOf(executionInfo.getId()));
        assessment.setDeprovisionJobStatus(executionInfo.getStatus());
        assessment.setRwaStatus(STATUS_INACTIVE);
        return assessment;
    }

    private ExecutionInfo runProvisioningJob(Candidate candidate, Region region) {
        JsonObject options = (JsonObject) new Gson()
                .toJsonTree(new RunDeckOptions(clean(candidate.getFirstName()), clean(candidate.getLastName()),
                        candidate.getEmail(), candidate.getUsername(), region.getName()));
        JobInfo provisionJob =
                JobInfo.builder().id(runDeckConfig.getRunDeckProvisionJob()).options(options).build();
        return runDeckClient.runJob(provisionJob);
    }

    private Region getRegion(String countryCode) {
        return StringUtils.isBlank(countryCode) ? defaultRegion
                : countryRegionRepository.findRegion(countryCode).orElse(defaultRegion);
    }

    @PostConstruct
    private void loadDefaultRegion() {
        defaultRegion = regionRepository.findByName(DEFAULT_REGION).get();
    }

    private ExecutionInfo runDeprovisionJob(String username) {
        JsonObject options = new JsonObject();
        options.addProperty("username", username);
        JobInfo deProvisionJob =
                JobInfo.builder().id(runDeckConfig.getRunDeckDeprovisionJob()).options(options).build();
        return runDeckClient.runJob(deProvisionJob);
    }

    private static String clean(String name) {
        return StringUtils.retainOnlyEnglishAlphabets(StringUtils.replaceDiacritics(name));
    }

    private static String trimUsername(String name) {
        if (name == null || name.length() <= MAX_LENGTH) {
            return name;
        }
        return name.substring(0, MAX_LENGTH);
    }
}
