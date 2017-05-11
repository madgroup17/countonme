package it.polito.mad.countonme.models;

public class ReportBackAction {

    public enum ActionEnum
    {
        ACTION_VIEW_SHARING_ACTIVITIES_LIST,
        ACTION_VIEW_SHARING_ACTIVITY_DETAIL_TABS,
        ACTION_ADD_NEW_SHARING_ACTIVITY,
        ACTION_EDIT_SHARING_ACTIVITY,
        ACTION_ACCEPT_SHARING_ACTIVITY,

        ACTION_VIEW_EXPENSE_DETAILS,
        ACTION_ADD_NEW_EXPENSE,
        ACTION_EDIT_EXPENSE,
        ACCEPT_REJECT_SA_FRAGMENT, ACTION_ACCEPT_EXPENSE_SURVEY
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
