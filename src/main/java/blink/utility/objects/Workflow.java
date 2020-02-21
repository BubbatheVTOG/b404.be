package blink.utility.objects;

import java.sql.Date;
import java.util.List;

public class Workflow {
    private int workflowID;
    private String name;
    private String description;
    private Date createdDate;
    private Date lastUpdatedDate;
    private Date startDate;
    private Date deliveryDate;
    private Date completedDate;
    private boolean archived;
    private int milestoneID;
    private double percentComplete;
    private int companyID;
    private List<Step> steps;

    public Workflow(int workflowID, String name, String description, Date createdDate, Date lastUpdatedDate, Date startDate, Date deliveryDate, Date completedDate, boolean archived, int companyID, int milestoneID, List<Step> steps) {
        this.workflowID = workflowID;
        this.description = description;
        this.name = name;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.completedDate = completedDate;
        this.archived = archived;
        this.companyID = companyID;
        this.milestoneID = milestoneID;
        this.steps = steps;
        this.percentComplete = this.calcPercentComplete();
    }

    public int getWorkflowID() {
        return workflowID;
    }

    public void setWorkflowID(int workflowID) {
        this.workflowID = workflowID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public int getMilestoneID() {
        return milestoneID;
    }

    public void setMilestoneID(int milestoneID) {
        this.milestoneID = milestoneID;
    }

    public double getPercentComplete() {
        return percentComplete;
    }

    private double calcPercentComplete() {
        double totalSteps = 0;
        double completeSteps = 0;
        for(Step step : this.steps){
            if(!step.getChildSteps().isEmpty()){
                Double[] updatedCounts = this.calcPercentComplete(step.getChildSteps(), totalSteps, completeSteps);
                totalSteps = updatedCounts[0];
                completeSteps = updatedCounts[1];
            }
            if(step.getCompleted()){
                completeSteps++;
            }
            totalSteps++;
        }
        return (totalSteps == 0) ? 1 : (completeSteps / totalSteps);
    }

    private Double[] calcPercentComplete(List<Step> steps, Double totalSteps, Double completeSteps) {
        for(Step step : steps){
            if(!step.getChildSteps().isEmpty()){
                Double[] updatedCounts = this.calcPercentComplete(step.getChildSteps(), totalSteps, completeSteps);
                totalSteps = updatedCounts[0];
                completeSteps = updatedCounts[1];
            }
            if(step.getCompleted()){
                completeSteps++;
            }
            totalSteps++;
        }
        return new Double[] {totalSteps, completeSteps};
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
}