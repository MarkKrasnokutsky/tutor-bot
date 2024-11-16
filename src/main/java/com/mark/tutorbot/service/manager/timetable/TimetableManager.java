package com.mark.tutorbot.service.manager.timetable;

import com.mark.tutorbot.entity.timetable.TimeTable;
import com.mark.tutorbot.entity.timetable.WeekDay;
import com.mark.tutorbot.entity.user.Action;
import com.mark.tutorbot.entity.user.Role;
import com.mark.tutorbot.entity.user.User;
import com.mark.tutorbot.entity.user.UserDetails;
import com.mark.tutorbot.repository.DetailsRepository;
import com.mark.tutorbot.repository.TimeTableRepository;
import com.mark.tutorbot.repository.UserRepository;
import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import com.mark.tutorbot.service.factory.KeyboardFactory;
import com.mark.tutorbot.service.manager.AbstractManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mark.tutorbot.service.data.CallbackData.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimetableManager extends AbstractManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;
    @Autowired
    private final KeyboardFactory keyboardFactory;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TimeTableRepository timeTableRepository;
    @Autowired
    private final DetailsRepository detailsRepository;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        var user = userRepository.findUserByChatId(message.getChatId());
        try {
            bot.execute(answerMethodFactory.getDeleteMessage(
                    message.getChatId(), message.getMessageId() - 1
            ));
            bot.execute(answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    "Значение успешно установлено",
                    null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        switch (user.getAction()) {
            case SENDING_TITLE -> {
                return setTittle(message, user);
            }
            case SENDING_DESCRIPTION -> {
                return setDescription(message, user);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        String[] splitCallbackData = callbackData.split("_");
        if (splitCallbackData.length > 1 && "add".equals(splitCallbackData[1])) {
            if (splitCallbackData.length == 2 || splitCallbackData.length == 3) {
                return add(callbackQuery, splitCallbackData);
            }
            switch (splitCallbackData[2]) {
                case WEEKDAY -> {
                    return addWeekDay(callbackQuery, splitCallbackData);
                }
                case HOUR -> {
                    return addHour(callbackQuery, splitCallbackData);
                }
                case MINUTE -> {
                    return addMinute(callbackQuery, splitCallbackData);
                }
                case USER -> {
                    return addUser(callbackQuery, splitCallbackData);
                }
                case TITTLE -> {
                    return askTittle(callbackQuery, splitCallbackData);
                }
                case DESCRIPTION -> {
                    return askDescription(callbackQuery, splitCallbackData);
                }
            }

        }
        if (FINISH.equals(splitCallbackData[1])) {
            try {
                return finish(callbackQuery, splitCallbackData, bot);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
        if (BACK.equals(splitCallbackData[1])) {
            return back(callbackQuery, splitCallbackData);
        }
        switch (callbackData) {
            case TIMETABLE -> {
                return mainMenu(callbackQuery);
            }
            case TIMETABLE_SHOW -> {
                return show(callbackQuery);
            }
            case TIMETABLE_REMOVE -> {
                return remove(callbackQuery);
            }
            case TIMETABLE_1, TIMETABLE_2, TIMETABLE_3,
                 TIMETABLE_4, TIMETABLE_5, TIMETABLE_6,
                 TIMETABLE_7 -> {
                return showDay(callbackQuery);
            }
        }
        return null;
    }

    private BotApiMethod<?> setDescription(Message message, User user) {
        user.setAction(Action.FREE);
        userRepository.save(user);
        String id = user.getDetails().getTimetableId();
        var timeTable = timeTableRepository.findTimeTableById(
                UUID.fromString(id)
        );
        timeTable.setDescription(message.getText());
        timeTableRepository.save(timeTable);
        return back(message, id);
    }

    private BotApiMethod<?> setTittle(Message message, User user) {
        user.setAction(Action.FREE);
        userRepository.save(user);
        String id = user.getDetails().getTimetableId();
        var timeTable = timeTableRepository.findTimeTableById(
                UUID.fromString(id)
        );
        timeTable.setTitle(message.getText());
        timeTableRepository.save(timeTable);
        return back(message, id);
    }

    private BotApiMethod<?> askDescription(CallbackQuery callbackQuery, String[] splitCallbackData) {
        String id = splitCallbackData[3];
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(Action.SENDING_DESCRIPTION);
        var details = user.getDetails();
        details.setTimetableId(id);
        detailsRepository.save(details);
        user.setDetails(details);
        userRepository.save(user);
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                "Введите описание:",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Назад"),
                        List.of(1),
                        List.of(TIMETABLE_BACK + id)
                )
        );
    }

    private BotApiMethod<?> askTittle(CallbackQuery callbackQuery, String[] splitCallbackData) {
        String id = splitCallbackData[3];
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(Action.SENDING_TITLE);
        var details = user.getDetails();
        details.setTimetableId(id);
        detailsRepository.save(details);
        user.setDetails(details);
        userRepository.save(user);
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                "Введите заголовок:",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Назад"),
                        List.of(1),
                        List.of(TIMETABLE_BACK + id)
                )
        );
    }

    private BotApiMethod<?> finish(CallbackQuery callbackQuery, String[] splitCallbackData, Bot bot)
            throws TelegramApiException {


        var timeTable = timeTableRepository.findTimeTableById(UUID.fromString(
                splitCallbackData[2]
        ));
        timeTable.setInCreation(false);
        timeTableRepository.save(timeTable);

        bot.execute(answerMethodFactory.getAnswerCallbackQuery(
                callbackQuery.getId(),
                "Процесс создания записи в расписании успешно завершен"
        ));
        return answerMethodFactory.getDeleteMessage(callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId());
    }

    private BotApiMethod<?> back(Message message, String id) {
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Вы можете настроить описание и заголовок",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Изменить заголовок", "Изменить описание",
                                "Завершить создание"),
                        List.of(2, 1),
                        List.of(TIMETABLE_ADD_TITTLE + id,
                                TIMETABLE_ADD_DESCRIPTION + id,
                                TIMETABLE_FINISH + id)
                )
        );
    }

    private BotApiMethod<?> back(CallbackQuery callbackQuery, String[] splitCallbackData) {
        String id = splitCallbackData[2];
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(Action.FREE);
        userRepository.save(user);
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                "Вы можете настроить описание и заголовок",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Изменить заголовок", "Изменить описание",
                                "Завершить создание"),
                        List.of(2, 1),
                        List.of(TIMETABLE_ADD_TITTLE + id,
                                TIMETABLE_ADD_DESCRIPTION + id,
                                TIMETABLE_FINISH + id)
                )
        );
    }

    private BotApiMethod<?> addUser(CallbackQuery callbackQuery, String[] splitCallbackData) {
        String id = splitCallbackData[4];
        var timeTable = timeTableRepository.findTimeTableById(UUID.fromString(id));
        var user = userRepository.findUserByChatId(Long.valueOf(splitCallbackData[3]));
        timeTable.addUser(user);
        timeTable.setTitle(user.getDetails().getFirstName());
        timeTableRepository.save(timeTable);
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                "Успешно! Запись добавлена, теперь вы можете настроить описание и заголовок",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Изменить заголовок", "Изменить описание",
                                "Завершить создание"),
                        List.of(2, 1),
                        List.of(TIMETABLE_ADD_TITTLE + id,
                                TIMETABLE_ADD_DESCRIPTION + id,
                                TIMETABLE_FINISH + id)
                )
        );
    }

    private BotApiMethod<?> addMinute(CallbackQuery callbackQuery, String[] splitCallbackData) {
        String id = splitCallbackData[4];
        var timeTable = timeTableRepository.findTimeTableById(UUID.fromString(id));
        List<String> text = new ArrayList<>();
        List<String> data = new ArrayList<>();
        List<Integer> cfg = new ArrayList<>();
        timeTable.setMinute(Short.valueOf(splitCallbackData[3]));
        int index = 0;
        var me = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        for (User user : me.getUsers()) {
            text.add(user.getDetails().getFirstName());
            data.add(TIMETABLE_ADD_USER + user.getChatId() + "_" + id);
            if (index == 5) {
                cfg.add(5);
                index = 0;
            } else {
                index += 1;
            }
        }
        if (index != 0) {
            cfg.add(index);
        }
        cfg.add(1);
        data.add(TIMETABLE_ADD_HOUR + timeTable.getHour() + "_" + id);
        text.add("Назад");
        timeTableRepository.save(timeTable);

        String messageText = "Выберете ученика";
        if (cfg.size() == 1) {
            messageText = "У вас нет ни одного ученика";
        }

        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                messageText,
                keyboardFactory.getInlineKeyboardMarkup(
                        text,
                        cfg,
                        data
                )
        );
    }

    private BotApiMethod<?> addHour(CallbackQuery callbackQuery, String[] splitCallbackData) {
        String id = splitCallbackData[4];
        var timeTable = timeTableRepository.findTimeTableById(UUID.fromString(id));
        List<String> text = new ArrayList<>();
        List<String> data = new ArrayList<>();
        timeTable.setHour(Short.valueOf(splitCallbackData[3]));
        for (int i = 0; i <= 59; i++) {
            text.add(String.valueOf(i));
            data.add(TIMETABLE_ADD_MINUTE + i + "_" + id);
        }
        text.add("Назад");
        switch (timeTable.getWeekDay()) {
            case MONDAY -> data.add(TIMETABLE_ADD_WEEKDAY + 1 + "_" + id);
            case TUESDAY -> data.add(TIMETABLE_ADD_WEEKDAY + 2 + "_" + id);
            case WEDNESDAY -> data.add(TIMETABLE_ADD_WEEKDAY + 3 + "_" + id);
            case THURSDAY -> data.add(TIMETABLE_ADD_WEEKDAY + 4 + "_" + id);
            case FRIDAY -> data.add(TIMETABLE_ADD_WEEKDAY + 5 + "_" + id);
            case SATURDAY -> data.add(TIMETABLE_ADD_WEEKDAY + 6 + "_" + id);
            case SUNDAY -> data.add(TIMETABLE_ADD_WEEKDAY + 7 + "_" + id);
        }
        timeTableRepository.save(timeTable);
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                "Выберете минуту",
                keyboardFactory.getInlineKeyboardMarkup(
                        text,
                        List.of(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1),
                        data
                )
        );
    }

    private BotApiMethod<?> addWeekDay(CallbackQuery callbackQuery, String[] data) {
        UUID id = UUID.fromString(data[4]);
        var timeTable = timeTableRepository.findTimeTableById(id);
        switch (data[3]) {
            case "1" -> timeTable.setWeekDay(WeekDay.MONDAY);
            case "2" -> timeTable.setWeekDay(WeekDay.TUESDAY);
            case "3" -> timeTable.setWeekDay(WeekDay.WEDNESDAY);
            case "4" -> timeTable.setWeekDay(WeekDay.THURSDAY);
            case "5" -> timeTable.setWeekDay(WeekDay.FRIDAY);
            case "6" -> timeTable.setWeekDay(WeekDay.SATURDAY);
            case "7" -> timeTable.setWeekDay(WeekDay.SUNDAY);
        }
        List<String> buttonsData = new ArrayList<>();
        List<String> text = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            text.add(String.valueOf(i));
            buttonsData.add(TIMETABLE_ADD_HOUR + i + "_" + data[4]);
        }
        buttonsData.add(TIMETABLE_ADD + "_" + data[4]);
        text.add("Назад");
        timeTableRepository.save(timeTable);
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                "Выберете час",
                keyboardFactory.getInlineKeyboardMarkup(
                        text,
                        List.of(6, 6, 6, 6, 1),
                        buttonsData
                )
        );
    }

    private BotApiMethod<?> mainMenu(Message message) {
        var user = userRepository.findUserByChatId(message.getChatId());
        if (user.getRole() == Role.STUDENT) {
            return answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    """
                            📆 Здесь вы можете посмотреть ваше расписание""",
                    keyboardFactory.getInlineKeyboardMarkup(
                            List.of("Показать мое расписание"),
                            List.of(1),
                            List.of(TIMETABLE_SHOW)
                    )
            );
        }
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        📆 Здесь вы можете управлять вашим расписанием""",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Показать мое расписание",
                                "Удалить занятие", "Добавить занятие"),
                        List.of(1, 2),
                        List.of(TIMETABLE_SHOW, TIMETABLE_REMOVE, TIMETABLE_ADD)
                )
        );
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        if (user.getRole() == Role.STUDENT) {
            return answerMethodFactory.getEditMessageText(
                    callbackQuery,
                    """
                            📆 Здесь вы можете посмотреть ваше расписание""",
                    keyboardFactory.getInlineKeyboardMarkup(
                            List.of("Показать мое расписание"),
                            List.of(1),
                            List.of(TIMETABLE_SHOW)
                    )
            );
        }
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                """
                        📆 Здесь вы можете управлять вашим расписанием""",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Показать мое расписание",
                                "Удалить занятие", "Добавить занятие"),
                        List.of(1, 2),
                        List.of(TIMETABLE_SHOW, TIMETABLE_REMOVE, TIMETABLE_ADD)
                )
        );
    }

    private BotApiMethod<?> showDay(CallbackQuery callbackQuery) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        WeekDay weekDay = WeekDay.MONDAY;
        switch (callbackQuery.getData().split("_")[1]) {
            case "2" -> weekDay = WeekDay.TUESDAY;
            case "3" -> weekDay = WeekDay.WEDNESDAY;
            case "4" -> weekDay = WeekDay.THURSDAY;
            case "5" -> weekDay = WeekDay.FRIDAY;
            case "6" -> weekDay = WeekDay.SATURDAY;
            case "7" -> weekDay = WeekDay.SUNDAY;
        }
        List<TimeTable> timeTableList = timeTableRepository
                .findAllByUsersContainingAndWeekDay(user, weekDay);
        StringBuilder text = new StringBuilder();
        if (timeTableList == null || timeTableList.isEmpty()) {
            text.append("У вас нет занятий в этот день!");
        } else {
            text.append("Ваши занятия сегодня:\n\n");
            for (TimeTable t : timeTableList) {
                text.append("▪\uFE0F ")
                        .append(t.getHour())
                        .append(":")
                        .append(t.getMinute())
                        .append(" - ")
                        .append(t.getTitle())
                        .append("\n\n");
            }
        }
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                text.toString(),
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("\uD83D\uDD19Назад"),
                        List.of(1),
                        List.of(TIMETABLE_SHOW)
                )
        );
    }

    private BotApiMethod<?> show(CallbackQuery callbackQuery) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                """
                        📆 Выберете день недели""",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of(
                                "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье",
                                "Назад"
                        ),
                        List.of(7, 1),
                        List.of(
                                TIMETABLE_1, TIMETABLE_2, TIMETABLE_3,
                                TIMETABLE_4, TIMETABLE_5, TIMETABLE_6, TIMETABLE_7,
                                TIMETABLE
                        )
                )
        );
    }

    private BotApiMethod<?> add(CallbackQuery callbackQuery, String[] splitCallbackData) {
        String id = "";
        if (splitCallbackData.length == 2) {
            var timeTable = new TimeTable();
            timeTable.addUser(userRepository.findUserByChatId(callbackQuery.getMessage().getChatId()));
            timeTable.setInCreation(true);
            id = timeTableRepository.save(timeTable).getId().toString();
        } else {
            id = splitCallbackData[2];
        }
        List<String> data = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            data.add(TIMETABLE_ADD_WEEKDAY + i + "_" + id);
        }
        data.add(TIMETABLE);
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                """
                        ✏️ Выберете день, в который хотите добавить занятие:""",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье",
                                "\uD83D\uDD19Назад"),
                        List.of(7, 1),
                        data
                )
        );
    }

    private BotApiMethod<?> remove(CallbackQuery callbackQuery) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                """
                        ✂️ Выберете занятие, которое хотите удалить из вашего расписания""",
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("\uD83D\uDD19Назад"),
                        List.of(1),
                        List.of(TIMETABLE)
                )
        );
    }
}
