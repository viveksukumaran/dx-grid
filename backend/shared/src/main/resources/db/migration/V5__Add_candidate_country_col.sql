alter table assessment 
add candidate_country int, 
add provisioning_region int,
add FOREIGN KEY (candidate_country) REFERENCES country(id),
add FOREIGN KEY (provisioning_region) REFERENCES region(id);