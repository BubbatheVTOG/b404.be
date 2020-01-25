package b404.utility.objects;

import java.sql.Date;

public class Milestone {
    private int mileStoneID;
    private int orderNumber;
    private String name;
    private String description;
    private Date deliveryDate;
    private int companyID;

    public Milestone(int mileStoneID, int orderNumber, String name, String description, Date deliveryDate, int companyID) {
        this.mileStoneID = mileStoneID;
        this.orderNumber = orderNumber;
        this.name = name;
        this.description = description;
        this.deliveryDate = deliveryDate;
        this.companyID = companyID;
    }

    public Milestone(int mileStoneID) { this.mileStoneID = mileStoneID; }

    public Milestone(String name) { this.name = name; }

    public int getMileStoneID() { return mileStoneID; }

    public void setMileStoneID(int mileStoneID) { this.mileStoneID = mileStoneID; }

    public int getOrderNumber() { return orderNumber; }

    public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Date getDeliveryDate() { return deliveryDate; }

    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public int getCompanyID() { return companyID; }

    public void setCompanyID(int companyID) { this.companyID = companyID; }
}