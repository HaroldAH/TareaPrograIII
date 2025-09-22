package com.tarea.resolvers.inputs;

import java.util.List;

public class UserInput {
    private Long id;
    private String name;
    private String email;
    private String password;

    private Boolean isAuditor = false;
    private Boolean isCoach   = false;    

    private Long assignedCoachId;         

    private List<ModulePermissionInput> permissions;  

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean getIsAuditor() { return isAuditor; }
    public void setIsAuditor(Boolean isAuditor) { this.isAuditor = isAuditor; }

    public Boolean getIsCoach() { return isCoach; }               
    public void setIsCoach(Boolean isCoach) { this.isCoach = isCoach; }  

    public Long getAssignedCoachId() { return assignedCoachId; }         
    public void setAssignedCoachId(Long assignedCoachId) { this.assignedCoachId = assignedCoachId; }  

    public List<ModulePermissionInput> getPermissions() { return permissions; }
    public void setPermissions(List<ModulePermissionInput> permissions) { this.permissions = permissions; }
}
