package blink.utility.objects;

import javax.ws.rs.BadRequestException;
import java.util.Date;
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
    private Company company;
    private List<Step> steps;
    private double percentComplete;

    public Workflow(int workflowID, String name, String description, Date createdDate, Date lastUpdatedDate, Date startDate, Date deliveryDate, Date completedDate, boolean archived, Company company, int milestoneID, List<Step> steps) {
        this.workflowID = workflowID;
        this.description = description;
        this.name = name;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.completedDate = completedDate;
        this.archived = archived;
        this.company = company;
        this.milestoneID = milestoneID;
        this.steps = steps;
        this.percentComplete = this.calcPercentComplete();

        //If completed and not already marked as complete, mark complete
        if(this.percentComplete == 1 && this.completedDate == null){
            this.completedDate = new Date();
        }
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
        try {
            double totalSteps = 0;
            double completeSteps = 0;
            for (Step step : this.steps) {
                if (!step.getChildSteps().isEmpty()) {
                    Double[] updatedCounts = this.calcPercentComplete(step.getChildSteps(), totalSteps, completeSteps);
                    totalSteps = updatedCounts[0];
                    completeSteps = updatedCounts[1];
                }
                if (step.getCompleted()) {
                    completeSteps++;
                }
                totalSteps++;
            }
            return (totalSteps == 0) ? 1 : (completeSteps / totalSteps);
        }
        catch(Exception e){
            throw new BadRequestException("Error in percent complete calculation");
        }
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Step> getSteps() {
        return this.steps;
    }

    public void setCompany(List<Step> steps) {
        this.steps = steps;
    }
}