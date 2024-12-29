package sg.edu.nus.iss.vttp5_mini_project.model;

import jakarta.validation.constraints.NotBlank;

public class SearchInfo {
    
    @NotBlank(message = "Search term is mandatory")
    private String query;

    public SearchInfo() {
    }

    public SearchInfo(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

}
