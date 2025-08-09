package kls.tgb.util;

public final class StringConstants {

    private StringConstants() {}

    // имена таблиц
    public static final String CONSTRUCTION_PROJECT = "construction_project";
    public static final String EXPENSE = "expense";
    public static final String EXPENSE_CATEGORY = "expense_category";
    public static final String USERS = "users";
    public static final String STATE = "state";

    // имена колонок
    public static final String TOTAL_BUDGET = "total_budget";
    public static final String END_DATE = "end_date";
    public static final String START_DATE = "start_date";
    public static final String USER_ID = "user_id";
    public static final String PROJECT = "project";
    public static final String PROJECT_ID = "project_id";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "category_id";
    public static final String TELEGRAM_ID = "telegram_id";
    public static final String USERNAME = "username";
    public static final String SELF_USERNAME = "self_username";
    public static final String ROLE = "role";
    public static final String CREATED_AT = "created_at";

    // константы бота
    public static final String SLASH = "/";
    public static final String START = "start";
    public static final String REMOVE = "remove";
    public static final String ADD_EXPENSE = "add_expense";

    // сообщения клиенту
    public static final String YOU_HAVE_NOT_PROJECTS = "У вас нет проектов. Хотите создать новый ?";
    public static final String YES = "Да";
    public static final String NO = "Нет";
    public static final String ITS_YOU_PROJECTS = "Вот ваши проекты. Выберите любой.";
    public static final String BYE_MESSAGE = "рад был познакомиться, пока!";
    public static final String ENTER_PROJECT_NAME = "введите название проекта";
    public static final String GREETINGS = "привет, ";
    public static final String LETS_SEE_PROJECTS = "  Перейдем к проектам ?";
    public static final String WHAT_IS_YOUR_NAME = "приветик, как тебя звать ?";
    public static final String ENTER_EXPENSE_NAME = "введите название расхода";
    public static final String ENTER_EXPENSE_AMOUNT = "введите величину расхода";

    // сообщения об ошибках
    public static final String CANT_FIND_COMMAND_HANDLER = "не найден обработчик команды";
    public static final String CHANGE_NAME_RESTRICTION = "пользователь не может менять имя после регистрации";

    // имена бинов
    public static final String EXPENSE_COMMAND_SM = "E_SM";
    public static final String START_COMMAND_SM = "S_SM";
    public static final String EXPENSE_COMMAND_SERVICE = "ECS";
    public static final String START_COMMAND_SERVICE = "SCS";


}
