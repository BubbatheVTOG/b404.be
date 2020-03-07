package blink.utility.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Step {
    private int stepID;
    private int orderNumber;
    @SerializedName("subtitle")
    private String description;
    private int parentStepID;
    private int uuid;
    @SerializedName("title")
    private int verbID;
    private int fileID;
    private int workflowID;
    private boolean asynchronous;
    private boolean completed;
    private boolean expanded;
    private List<Step> children;

    private Step(StepBuilder stepBuilder) {
        this.stepID = stepBuilder.stepID;
        this.orderNumber = stepBuilder.orderNumber;
        this.description = stepBuilder.description;
        this.parentStepID = stepBuilder.parentStepID;
        this.uuid = stepBuilder.uuid;
        this.verbID = stepBuilder.verbID;
        this.fileID = stepBuilder.fileID;
        this.workflowID = stepBuilder.workflowID;
        this.asynchronous = stepBuilder.asynchronous;
        this.completed = stepBuilder.completed;
        this.expanded = stepBuilder.expanded;
        this.children = stepBuilder.childSteps;
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
        private boolean asynchronous;
        private boolean completed;
        private boolean expanded;
        private List<Step> childSteps;

        public StepBuilder(int workflowID, boolean asynchronous, boolean completed) {
            this.workflowID = workflowID;
            this.asynchronous = asynchronous;
            this.completed = completed;
        }

        public StepBuilder stepID(int stepID) {
            this.stepID = stepID;
            return this;
        }

        public StepBuilder orderNumber(int orderNumber) {
            this.orderNumber = orderNumber;
            return this;
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

        public StepBuilder verbID(int verbID) {
            this.verbID = verbID;
            return this;
        }

        public StepBuilder fileID(int fileID) {
            this.fileID = fileID;
            return this;
        }

        public StepBuilder childSteps(List<Step> childSteps) {
            this.childSteps = childSteps;
            this.expanded = !this.childSteps.isEmpty();
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

    public boolean isCompleted() { return completed; }

    public boolean getExpanded() { return expanded; }

    public boolean getAsynchronous() { return asynchronous; }

    public void setAsynchronous(boolean asynchronous) { this.asynchronous = asynchronous; }

    public List<Step> getChildren() { return children; }

    public void setChildren(List<Step> children) {
        this.children = children;
        this.expanded = this.hasChildren();
    }

    public boolean hasChildren() {
        boolean fdsa = children == null;
        return !(children == null || children.isEmpty()); }

}
