package de.hype.bingonet.client.common.chat;

import de.hype.bingonet.client.common.annotations.AnnotationProcessor;

public abstract class MessageEvent implements AnnotationProcessor.Event {
    public final Message message;
    private Message modified;
    private boolean canceled;

    public MessageEvent(Message message) {
        this.message = message;
    }

    /**
     * Not useabale since edits can not be applied!
     */
    @Deprecated
    public void setModifiedMessage(Message modified) {
        this.modified = modified;
    }

    public Message getMessageOrModified() {
        if (modified!=null) return modified;
        return message;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void cancel(){
        canceled=true;
    }

    public abstract void deleteFromChat(int lineAmount);

    /**
     * Not useabale since edits can not be applied!
     */
    @Deprecated
    public boolean isModified() {
        return modified!=null;
    }
}
