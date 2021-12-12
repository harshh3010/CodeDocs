package utilities;

public class DatabaseConstants {

    // USER AUTHENTICATION TABLE
    public static final String USER_TABLE_NAME = "user_table";
    public static final String USER_TABLE_COL_EMAIL = "email";
    public static final String USER_TABLE_COL_PASSWORD = "password";
    public static final String USER_TABLE_COL_FIRSTNAME = "firstname";
    public static final String USER_TABLE_COL_LASTNAME = "lastname";
    public static final String USER_TABLE_COL_ISVERIFIED = "isVerified";
    public static final String USER_TABLE_COL_USERID = "userID";

    // USER VERIFICATION TABLE
    public static final String USER_VERIFICATION_TABLE_NAME = "user_verification_table";
    public static final String USER_VERIFICATION_TABLE_COL_USER_ID = "userID";
    public static final String USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN = "verificationToken";
    public static final String USER_VERIFICATION_TABLE_COL_EXPIRES_AT = "expiresAt";

    // CODEDOC TABLE
    public static final String CODEDOC_TABLE_NAME = "codedoc_table";
    public static final String CODEDOC_TABLE_COL_CODEDOCID = "codedocID";
    public static final String CODEDOC_TABLE_COL_TITLE = "title";
    public static final String CODEDOC_TABLE_COL_DESCRIPTION = "description";
    public static final String CODEDOC_TABLE_COL_LANGUAGE = "language";
    public static final String CODEDOC_TABLE_COL_OWNERID = "ownerID";
    public static final String CODEDOC_TABLE_COL_CREATED_AT = "createdAt";
    public static final String CODEDOC_TABLE_COL_UPDATED_AT = "updatedAt";

    //CODEDOC ACCESS TABLE
    public static final String CODEDOC_ACCESS_TABLE_NAME = "codedoc_access_table";
    public static final String CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID = "codedocID";
    public static final String CODEDOC_ACCESS_TABLE_COL_USER_ID = "userID";
    public static final String CODEDOC_ACCESS_TABLE_COL_IS_OWNER = "isOwner";
    public static final String CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS = "hasWritePermissions";
    public static final String CODEDOC_ACCESS_TABLE_COL_IS_ACTIVE = "isActive";


}