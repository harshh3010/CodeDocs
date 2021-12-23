package utilities;

import controllers.codeDocs.MyLibraryTabController;
import controllers.codeDocs.PersonalCodeDocsTabController;
import controllers.invitations.InvitationsTabController;
import controllers.notes.MyNotesTabController;

public class TabController {

    private MyLibraryTabController myLibraryTabController;
    private MyNotesTabController myNotesTabController;
    private PersonalCodeDocsTabController personalCodeDocsTabController;
    private InvitationsTabController invitationsTabController;

    private static TabController instance = null;

    // private constructor restricted to this class itself
    private TabController() {
    }

    // static method to create instance of Singleton class
    public static TabController getInstance() {
        if (instance == null)
            instance = new TabController();
        return instance;
    }

    public MyLibraryTabController getMyLibraryTabController() {
        return myLibraryTabController;
    }

    public void setMyLibraryTabController(MyLibraryTabController myLibraryTabController) {
        this.myLibraryTabController = myLibraryTabController;
    }

    public MyNotesTabController getMyNotesTabController() {
        return myNotesTabController;
    }

    public void setMyNotesTabController(MyNotesTabController myNotesTabController) {
        this.myNotesTabController = myNotesTabController;
    }

    public PersonalCodeDocsTabController getPersonalCodeDocsTabController() {
        return personalCodeDocsTabController;
    }

    public void setPersonalCodeDocsTabController(PersonalCodeDocsTabController personalCodeDocsTabController) {
        this.personalCodeDocsTabController = personalCodeDocsTabController;
    }

    public InvitationsTabController getInvitationsTabController() {
        return invitationsTabController;
    }

    public void setInvitationsTabController(InvitationsTabController invitationsTabController) {
        this.invitationsTabController = invitationsTabController;
    }
}
