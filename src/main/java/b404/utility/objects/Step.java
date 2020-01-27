package b404.utility.objects;

import java.util.ArrayList;

public class Step {
    private int stepID;
    private int orderNumber;
    private boolean isHighestLevel;
    private String description;
    private int relatedStep;
    private int UUID;
    private int verbID;
    private int fileID;
    private int workflowID;
    private ArrayList<Step> childSteps;

    public Step(int stepID, int orderNumber, boolean isHigestLevel, String description, int relatedStep, int UUID, int verbID, int fileID, int workflowID) {
        this.stepID = stepID;
        this.orderNumber = orderNumber;
        this.isHighestLevel = isHighestLevel;
        this.description = description;
        this.relatedStep = relatedStep;
        this.UUID = UUID;
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

    public int getUUID() { return UUID; }

    public void setUUID(int UUID) { this.UUID = UUID; }

    public int getVerbID() { return verbID; }

    public void setVerbID(int verbID) { this.verbID = verbID; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public int getWorkflowID() { return workflowID; }

    public void setWorkflowID(int workflowID) { this.workflowID = workflowID; }

    public ArrayList<Step> getChildSteps() { return childSteps; }

    public void setChildSteps(ArrayList<Step> childSteps) { this.childSteps = childSteps; }

    /**
     * Add children steps to parent step
     * @param child - x number of child steps to add to parent step
     */
    public void addChild(Step... child) {
        for(int i = 0; i < child.length; i++) {
            this.childSteps.add(child[i]);
        }
    }
}
