package it.polito.mad.countonme.models;

public class ReportBackAction {

    public enum ActionEnum
    {
        ACTION_ADD_NEW_SHARING_ACTIVITY,
        ACTION_VIEW_SHARING_ACTIVITIES_LIST,
        ACTION_VIEW_SHARING_ACTIVITY,
        ACTION_MODIFY_SHARING_ACTIVITY,
        ACTION_DELETE_SHARING_ACTIVITY,

        ACTION_ADD_NEW_EXPENSE,
        ACTION_VIEW_EXPENSES_LIST,
        ACTION_VIEW_EXPENSE,
        ACTION_MODIFY_EXPENSE,
        ACTION_DELETE_EXPENSE,

        ACTION_VIEW_BALANCE
    }

    private ActionEnum mAction;
    private Object mActionData;

    public ReportBackAction( ActionEnum action, Object actionData )
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
