package it.polito.mad.countonme.models;

public class ReportBackAction {

    public enum ActionEnum
    {
        ADD_NEW_SHARING_ACTIVITY
    }

    private ActionEnum mAction;
    private Object mActionData;

    ReportBackAction( ActionEnum action, Object actionData )
    {
        mAction = action;
        mActionData = actionData;
    }

    public ActionEnum getAction() {
        return mAction;
    }

    public Object getActionData() {
        return mActionData;
    }

}
