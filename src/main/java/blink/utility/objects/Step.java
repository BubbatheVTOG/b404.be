package blink.utility.objects;

import java.util.List;

public class Step {
    private int stepID;
    private int orderNumber;
    private boolean isHighestLevel;
    private String description;
    private int relatedStep;
    private int uuid;
    private int verbID;
    private int fileID;
    private int workflowID;
    private List<Step> childSteps;

    public Step(int stepID, int orderNumber, boolean isHigestLevel, String description, int relatedStep, int uuid, int verbID, int fileID, int workflowID) {
        this.stepID = stepID;
        this.orderNumber = orderNumber;
        this.isHighestLevel = isHigestLevel;
        this.description = description;
        this.relatedStep = relatedStep;
        this.uuid = uuid;
        this.verbID = verbID;
        this.fileID = fileID;
        this.workflowID = workflowID;
    }

    public int getStepID() { return  stepID; }

    public void setStepID(int stepID) { this.stepID = stepID; }

    public int getOrderNumber() { return orderNumber; }

    public void setOrderNumber(int orderNumber) { this.orderNumber = orderNumber; }

    public boolean getIsHighestLevel() { return isHighestLevel; }

    public void setHighestLevel(boolean isHighestLevel) { this.isHighestLevel = isHighestLevel; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getRelatedStep() { return relatedStep; }

    public void setRelatedStep(int relatedStep) { this.relatedStep = relatedStep; }

    public int getUUID() { return uuid; }

    public void setUUID(int uuid) { this.uuid = uuid; }

    public int getVerbID() { return verbID; }

    public void setVerbID(int verbID) { this.verbID = verbID; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public int getWorkflowID() { return workflowID; }

    public void setWorkflowID(int workflowID) { this.workflowID = workflowID; }

    public List<Step> getChildSteps() { return childSteps; }

    public void setChildSteps(List<Step> childSteps) { this.childSteps = childSteps; }

    /**
     * Add children steps to parent step
     * @param child - x number of child steps to add to parent step
     */
    public void addChild(Step... child) {
        for(int i = 0; i < child.length; i++) {
            this.childSteps.add(child[i]);
        }
    }

    public String toJSON(){
        return "{" +
                "\"stepID\":" + this.stepID + "," +
                "\"orderNumber\":\"" + this.orderNumber + "\"," +
                "\"isHighestLevel\":" + this.isHighestLevel + "," +
                "\"description\":" + this.description + "," +
                "\"relatedStep\":" + this.relatedStep + "," +
                "\"UUID\":" + this.uuid + "," +
                "\"verbID\":" + this.verbID + "," +
                "\"fileID\":" + this.fileID + "," +
                "\"workflowID\":" + this.workflowID + "," +
                "\"childSteps\":" + this.childSteps + "," +
                "}";
    }
}
