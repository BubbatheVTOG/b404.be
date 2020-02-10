package blink.utility.objects;

public class Step {
    private int UUID;
    private int orderNumber;
    private String description;
    private int relatedStep;
    private int workflowID;

    public Step(int UUID, int orderNumber, String description, int relatedStep, int workflowID) {
        this.UUID = UUID;
        this.orderNumber = orderNumber;
        this.description = description;
        this.relatedStep = relatedStep;
        this.workflowID = workflowID;
    }

    public Step(int UUID, int orderNumber, String description, int workflowID) {
        this.UUID = UUID;
        this.orderNumber = orderNumber;
        this.description = description;
        this.workflowID = workflowID;
    }

    public Step(int UUID, String description, int relatedStep, int workflowID) {
        this.UUID = UUID;
        this.description = description;
        this.relatedStep = relatedStep;
        this.workflowID = workflowID;
    }

    public Step(int UUID, int orderNumber, int relatedStep, int workflowID) {
        this.UUID = UUID;
        this.orderNumber = orderNumber;
        this.relatedStep = relatedStep;
        this.workflowID = workflowID;
    }

    public Step(int UUID, int workflowID) {
        this.UUID = UUID;
        this.workflowID = workflowID;
    }

    public int getUUID() { return UUID; }

    public void setUUID(int UUID) { this.UUID = UUID; }

    public int getOrderNumber() { return orderNumber; }

    public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getRelatedStep() { return relatedStep; }

    public void setRelatedStep(int relatedStep) { this.relatedStep = relatedStep; }

    public int getWorkflowID() { return workflowID; }

    public void setWorkflowID(int workflowID) { this.workflowID = workflowID; }
}
