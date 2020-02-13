package blink.utility.objects;

import java.util.Arrays;
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
    private boolean completed;
    private List<Step> childSteps;

    private  Step(StepBuilder stepBuilder) {
        this.stepID = stepBuilder.stepID;
        this.orderNumber = stepBuilder.orderNumber;
        this.isHighestLevel = stepBuilder.isHighestLevel;
        this.description = stepBuilder.description;
        this.relatedStep = stepBuilder.relatedStep;
        this.uuid = stepBuilder.uuid;
        this.verbID = stepBuilder.verbID;
        this.fileID = stepBuilder.fileID;
        this.workflowID = stepBuilder.workflowID;
        this.completed = stepBuilder.completed;
        this.childSteps = stepBuilder.childSteps;
    }

    public static class StepBuilder {
        private int stepID;
        private int orderNumber;
        private boolean isHighestLevel;
        private String description;
        private int relatedStep;
        private int uuid;
        private int verbID;
        private int fileID;
        private int workflowID;
        private boolean completed;
        private List<Step> childSteps;

        public StepBuilder(int stepID, int orderNumber, boolean isHighestLevel, int verbID, int fileID, int workflowID, boolean completed) {
            this.stepID = stepID;
            this.orderNumber = orderNumber;
            this.isHighestLevel = isHighestLevel;
            this.verbID = verbID;
            this.fileID = fileID;
            this.workflowID = workflowID;
            this.completed = completed;
        }

        public StepBuilder description(String description) {
            this.description = description;
            return this;
        }

        public StepBuilder relatedStep(int relatedStep) {
            this.relatedStep = relatedStep;
            return this;
        }

        public StepBuilder uuid(int uuid) {
            this.uuid = uuid;
            return this;
        }

        public StepBuilder childSteps(List<Step> childSteps) {
            this.childSteps = childSteps;
            return this;
        }

        public Step build() {
            return new Step(this);
        }
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

    public boolean getCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public List<Step> getChildSteps() { return childSteps; }

    public void setChildSteps(List<Step> childSteps) { this.childSteps = childSteps; }

    /**
     * Add children steps to parent step
     * @param child - x number of child steps to add to parent step
     **/
    public void addChild(Step... child) {
        this.childSteps.addAll(0, Arrays.asList(child));
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
