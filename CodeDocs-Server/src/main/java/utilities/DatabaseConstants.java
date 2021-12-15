package utilities;

public class DatabaseConstants {

    // USER AUTHENTICATION TABLE
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_TABLE_COL_EMAIL = "email";
    public static final String USER_TABLE_COL_PASSWORD = "password";
    public static final String USER_TABLE_COL_FIRSTNAME = "first_name";
    public static final String USER_TABLE_COL_LASTNAME = "last_name";
    public static final String USER_TABLE_COL_ISVERIFIED = "is_verified";
    public static final String USER_TABLE_COL_USERID = "user_id";

    // USER VERIFICATION TABLE
    public static final String USER_VERIFICATION_TABLE_NAME = "user_verification";
    public static final String USER_VERIFICATION_TABLE_COL_USER_ID = "user_id";
    public static final String USER_VERIFICATION_TABLE_COL_VERIFICATION_TOKEN = "verification_token";
    public static final String USER_VERIFICATION_TABLE_COL_EXPIRES_AT = "expires_at";

    // CODEDOC TABLE
    public static final String CODEDOC_TABLE_NAME = "codedoc";
    public static final String CODEDOC_TABLE_COL_CODEDOCID = "codedoc_id";
    public static final String CODEDOC_TABLE_COL_TITLE = "title";
    public static final String CODEDOC_TABLE_COL_DESCRIPTION = "description";
    public static final String CODEDOC_TABLE_COL_LANGUAGE = "language";
    public static final String CODEDOC_TABLE_COL_OWNERID = "owner_id";
    public static final String CODEDOC_TABLE_COL_CREATED_AT = "created_at";
    public static final String CODEDOC_TABLE_COL_UPDATED_AT = "updated_at";

    //CODEDOC ACCESS TABLE
    public static final String CODEDOC_ACCESS_TABLE_NAME = "codedoc_accessor";
    public static final String CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID = "codedoc_id";
    public static final String CODEDOC_ACCESS_TABLE_COL_USER_ID = "user_id";
    public static final String CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT = "access_right";
    public static final String CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS = "has_write_permissions";
    public static final String CODEDOC_ACCESS_TABLE_COL_IS_ACTIVE = "is_active";


}