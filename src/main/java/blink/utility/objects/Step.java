package blink.utility.objects;

import java.util.List;

public class Step {
    private int stepID;
    private int orderNumber;
    private String description;
    private int parentStepID;
    private int uuid;
    private int verbID;
    private int fileID;
    private int workflowID;
    private boolean completed;
    private List<Step> childSteps;

    private  Step(StepBuilder stepBuilder) {
        this.stepID = stepBuilder.stepID;
        this.orderNumber = stepBuilder.orderNumber;
        this.description = stepBuilder.description;
        this.parentStepID = stepBuilder.parentStepID;
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
        private String description;
        private int parentStepID;
        private int uuid;
        private int verbID;
        private int fileID;
        private int workflowID;
        private boolean completed;
        private List<Step> childSteps;

        public StepBuilder(int stepID, int orderNumber, int verbID, int fileID, int workflowID, boolean completed) {
            this.stepID = stepID;
            this.orderNumber = orderNumber;
            this.parentStepID = 0;
            this.verbID = verbID;
            this.fileID = fileID;
            this.workflowID = workflowID;
            this.completed = completed;
        }

        public StepBuilder description(String description) {
            this.description = description;
            return this;
        }

        public StepBuilder parentStep(int parentStepID) {
            this.parentStepID = parentStepID;
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

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getParentStepID() { return parentStepID; }

    public void setParentStepID(int parentStepID) { this.parentStepID = parentStepID; }

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

}
