package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    CSS_RESOURCE_PATH,
    CSS_RESOURCE_FILENAME,
    LINECHART_FILENAME,
    TEXTAREA_FILENAME,
    GUI_RESOURCE_PATH,
    ICONS_RESOURCE_PATH,

    /* Displaying loaded information*/
    INSTANCE_COUNT,
    LABEL_COUNT,
    LABEL_LIST,
    NO_FILE_FOUND,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,
    VALIDATE,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,
    ERROR_TITLE,
    EXIT_WHILE_RUNNING_TITLE,
    EXIT_WHILE_RUNNING_WARNING,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,
    ERROR_DATA,
    ERROR_AT_LINE,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,

    /* Choice Box Area */
    CLASSIFICATION,
    CLUSTERING,
    SELECT_ALGORITHM_TYPE,

    /*Algorithm Configuration */
    MAX_ITERATION,
    NUMBER_OF_CLUSTERS,
    CONTINUOUS_RUN,
    UPDATE_INTERVAL,
    RUNTIME_CONFIGURATION,
    FINISHED_RUNNING_ALGORITHM,
    UNFINISHED_RUNNING_ALGORITHM,

    DISPLAY,
    READ_ONLY,
    CHART_TITLE,
    CONFIGURE,

}
