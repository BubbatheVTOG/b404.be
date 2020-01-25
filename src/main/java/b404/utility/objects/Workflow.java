package b404.utility.objects;

import java.sql.Date;

public class Workflow {
    private int workflowID;
    private String name;
    private Date startDate;
    private Date endDate;
    private int milestoneID;

    public Workflow(int workflowID, String name, Date startDate, Date endDate, int milestoneID) {
        this.workflowID = workflowID;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.milestoneID = milestoneID;
    }

    public Workflow(int workflowID) { this.workflowID = workflowID; }

    public Workflow(String name) { this.name = name; }

    public int getWorkflowID() { return workflowID; }

    public void setWorkflowID(int workflowID) { this.workflowID = workflowID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public int getMilestoneID() { return milestoneID; }

    public void setMilestoneID(int milestoneID) { this.milestoneID = milestoneID; }
}