package db.migration;

import com.devfactory.processautomation.qa.rwa.service.dtos.AssessmentRequest;
import com.devfactory.processautomation.qa.rwa.service.dtos.Candidate;
import com.google.gson.Gson;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V2__CreateRequestFields extends BaseJavaMigration {

    private static final String ALTER = "alter table assessment "
            + "add salesforce_order_id varchar(50), add candidate_name varchar(100), add candidate_email varchar(50), "
            + "add username varchar(50), add vendor_id varchar(50), add test_id varchar(50), "
            + "add rwa_status varchar(30), add candidate_first_name varchar(50), add candidate_last_name varchar(50)";
    private static final String SELECT =
            "select id, request_json, provisioning_status, deprovision_job_id from assessment";
    private static final int ID = 1;
    private static final int JSON = 2;
    private static final int STATUS = 3;
    private static final int DEPROVISION_JOB_ID = 4;
    private static final String UPDATE = "update assessment set salesforce_order_id = ?, candidate_name = ?, "
            + "candidate_email = ?, username = ?,  vendor_id = ?, test_id = ?,"
            + "rwa_status = ?, candidate_first_name = ?, candidate_last_name = ? where id = ?";
    private static final Pattern CALLBACK_URL =
            Pattern.compile("https?://.+/services/apexrest/assessments/v1/(.+)/(.+)");
    private static final int GROUP_ORDER_ID = 2;
    private static final int GROUP_VENDOR_ID = 1;
    private static final String EMPTY = "";

    public void migrate(Context context) throws SQLException {
        try (Statement alter = context.getConnection().createStatement()) {
            alter.execute(ALTER);
            populateFields(context);
        }
    }

    private void populateFields(Context context) throws SQLException {
        try (Statement select = context.getConnection().createStatement()) {
            try (ResultSet rows = select.executeQuery(SELECT)) {
                try (PreparedStatement update = context.getConnection().prepareStatement(UPDATE)) {
                    while (rows.next()) {
                        int id = rows.getInt(ID);
                        AssessmentRequest request = new Gson().fromJson(rows.getString(JSON), AssessmentRequest.class);
                        Candidate candidate = request.getCandidate();

                        update.setString(1, getSalesforceOrderId(request.getCallbackUrl()));
                        update.setString(2, candidate.getFirstName() + " " + candidate.getLastName());
                        update.setString(3, candidate.getEmail());
                        update.setString(4, candidate.getUsername());
                        update.setString(5, getVendorId(request.getCallbackUrl()));
                        update.setString(6, request.getTestId());
                        update.setString(7, getRwaStatus(rows.getString(STATUS), rows.getString(DEPROVISION_JOB_ID)));
                        update.setString(8, candidate.getFirstName());
                        update.setString(9, candidate.getLastName());
                        update.setInt(10, id);

                        update.addBatch();
                    }
                    update.executeBatch();
                }
            }
        }
    }

    private String getRwaStatus(String provisioningStatus, String deprovisionJobId) {
        if (deprovisionJobId != null) {
            return "inactive";
        }
        if ("running".equals(provisioningStatus)) {
            return "in progress";
        }
        if ("succeeded".equals(provisioningStatus)) {
            return "active";
        }
        return "failed";
    }

    private static String getSalesforceOrderId(String callBackUrl) {
        return parseCallbackUrl(callBackUrl, GROUP_ORDER_ID).orElse(EMPTY);
    }

    private static String getVendorId(String callBackUrl) {
        return parseCallbackUrl(callBackUrl, GROUP_VENDOR_ID).orElse(EMPTY);
    }

    private static Optional<String> parseCallbackUrl(String callBackUrl, int group) {
        Matcher matcher = CALLBACK_URL.matcher(callBackUrl);
        return matcher.matches() ? Optional.of(matcher.group(group)) : Optional.empty();
    }
}