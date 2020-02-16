package blink.utility.objects;

import com.google.gson.Gson;

import java.util.Date;

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
    private int companyID;
    private  transient Gson gson;

    /**
     * Construct a milestone and provide all information necessary
     * @param mileStoneID - ID for the milestone
     * @param name - name for the workflow
     * @param description - general description for the workflow
     * @param createdDate - date workflow was created
     * @param lastUpdatedDate - date workflow was updated last
     * @param deliveryDate - date workflow is scheduled for delivery
     * @param completedDate - date workflow was completed
     * @param archived - whether the workflow is archived or not
     * @param companyID - company that the workflow is assigned to
     */
    public Milestone(int mileStoneID, String name, String description, Date createdDate, Date lastUpdatedDate, Date startDate, Date deliveryDate, Date completedDate, boolean archived, int companyID) {
        this.mileStoneID = mileStoneID;
        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.completedDate = completedDate;
        this.archived = archived;
        this.companyID = companyID;

        this.gson = new Gson();
    }

    public Milestone(int mileStoneID, String name, String description, Date startDate, Date deliveryDate, int companyID) {
        this.mileStoneID = mileStoneID;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.deliveryDate = deliveryDate;
        this.companyID = companyID;

        this.gson = new Gson();
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
        return this.deliveryDate == null;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
}