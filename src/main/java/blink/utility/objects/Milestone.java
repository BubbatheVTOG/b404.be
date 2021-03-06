package blink.utility.objects;

import blink.businesslayer.WorkflowBusiness;

import java.util.Date;
import java.util.List;

public class Milestone {
    private int mileStoneID;
    private String name;
    private String description;
    private Date createdDate;
    private Date lastUpdatedDate;
    private Date startDate;
    private Date deliveryDate;
    private Date completedDate;
    private boolean archived;
    private Company company;
    private double percentComplete;

    /**
     * Construct a milestone and provide all information necessary
     * @param mileStoneID ID for the milestone
     * @param name name for the workflow
     * @param description general description for the workflow
     * @param createdDate date workflow was created
     * @param lastUpdatedDate date workflow was updated last
     * @param deliveryDate date workflow is scheduled for delivery
     * @param completedDate date workflow was completed
     * @param archived whether the workflow is archived or not
     * @param company company that the workflow is assigned to
     */
    public Milestone(int mileStoneID, String name, String description, Date createdDate, Date lastUpdatedDate, Date startDate, Date deliveryDate, Date completedDate, boolean archived, Company company) {
        this.mileStoneID = mileStoneID;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.completedDate = completedDate;
        this.archived = archived;
        this.company = company;
    }

    public Milestone(int mileStoneID, String name, String description, Date startDate, Date deliveryDate, Company company) {
        this.mileStoneID = mileStoneID;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.company = company;
    }

    public int getMileStoneID() {
        return mileStoneID;
    }

    public void setMileStoneID(int mileStoneID) {
        this.mileStoneID = mileStoneID;
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

    public boolean isCompleted() {
        return this.percentComplete == 1.0;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public double getPercentComplete() {
        return this.percentComplete;
    }

    public void setPercentComplete(String uuid) {
        this.percentComplete = calculateCompletion(uuid, this.mileStoneID);
        //If completed and not already marked as complete, mark complete
        if(this.percentComplete == 1.0 && this.completedDate == null){
            this.completedDate = new Date();
        }
    }

    public static double calculateCompletion(String uuid, int milestoneID) {
        WorkflowBusiness workflowBusiness = new WorkflowBusiness();
        List<Workflow> workflowList = workflowBusiness.getWorkflowsByMilestoneID(uuid, Integer.toString(milestoneID));

        if(workflowList.isEmpty()){
            return 0.0;
        }

        double completion = 0.0;

        for(Workflow workflow : workflowList) {
            completion += workflow.getPercentComplete();
        }

        return (completion / (double) workflowList.size());
    }
}