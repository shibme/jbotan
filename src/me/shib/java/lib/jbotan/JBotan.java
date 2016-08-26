package me.shib.java.lib.jbotan;

import me.shib.java.lib.botan.Botan;
import me.shib.java.lib.jbotstats.BotStatsConfig;
import me.shib.java.lib.jbotstats.JBotStats;
import me.shib.java.lib.jtelebot.models.inline.InlineKeyboardMarkup;
import me.shib.java.lib.jtelebot.models.inline.InlineQueryResult;
import me.shib.java.lib.jtelebot.models.types.*;
import me.shib.java.lib.jtelebot.models.updates.ChosenInlineResult;
import me.shib.java.lib.jtelebot.models.updates.InlineQuery;
import me.shib.java.lib.jtelebot.models.updates.Message;
import me.shib.java.lib.jtelebot.models.updates.Update;
import me.shib.java.lib.restiny.util.JsonUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JBotan extends JBotStats {

    private static final Logger logger = Logger.getLogger(JBotan.class.getName());

    private Botan botan;
    private User botInfo;
    private JsonUtil jsonUtil;

    public JBotan(BotStatsConfig botStatsConfig, User botInfo) {
        super(botStatsConfig, botInfo);
        if ((botStatsConfig.getToken() != null) && (!botStatsConfig.getToken().isEmpty())) {
            this.botan = new Botan(botStatsConfig.getToken());
        }
        this.botInfo = botInfo;
        this.jsonUtil = new JsonUtil();
    }

    private void trackData(long user_id, String name, Object data) {
        logger.log(Level.FINEST, "Tracking Data: [user_id: " + user_id + ", name: " + name + "]\ndata: " + jsonUtil.toJson(data));
        if (botan != null) {
            if ((name != null) && (!name.isEmpty()) && (data != null)) {
                try {
                    botan.track(user_id, name, data);
                } catch (IOException e) {
                    logger.throwing(this.getClass().getName(), "trackData", e);
                }
            }
        }
    }

    private long getLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getAnalyticsRedirectedURL(long user_id, String url) {
        logger.entering(this.getClass().getName(), "getAnalyticsRedirectedURL", new Object[]{user_id, url});
        if (botan != null) {
            try {
                return botan.shortenURL(user_id, url);
            } catch (IOException e) {
                logger.throwing(this.getClass().getName(), "getAnalyticsRedirectedURL", e);
            }
        }
        return null;
    }

    @Override
    public void onReceivingUpdate(Update update, IOException e, Date accessTime) {
        if (update.getMessage() != null) {
            Message message = update.getMessage();
            trackData(message.getFrom().getId(), "Received Message", message);
        } else if (update.getInline_query() != null) {
            InlineQuery inlineQuery = update.getInline_query();
            trackData(inlineQuery.getFrom().getId(), "Inline Query", inlineQuery);
        } else if (update.getChosen_inline_result() != null) {
            ChosenInlineResult chosenInlineResult = update.getChosen_inline_result();
            trackData(chosenInlineResult.getFrom().getId(), "Chosen Inline Result", chosenInlineResult);
        }
    }

    @Override
    public void onGettingMe(User response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), "Getting Bot Info", data);
    }

    @Override
    public void onGettingUserProfilePhotos(long user_id, int offset, int limit, UserProfilePhotos response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("user_id", user_id);
            data.put("offset", offset);
            data.put("limit", limit);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(user_id, "Getting User Profile Photo", data);
    }

    @Override
    public void onGettingFile(String file_id, TFile response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("file_id", file_id);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), "Getting a File Info", data);
    }

    @Override
    public void onSendingMessage(ChatId chat_id, String text, ParseMode parse_mode, boolean disable_web_page_preview, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("text", text);
            data.put("disable_notification", disable_notification);
            data.put("parse_mode", parse_mode);
            data.put("disable_web_page_preview", disable_web_page_preview);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Text", data);
    }

    @Override
    public void onForwardingMessage(ChatId chat_id, ChatId from_chat_id, long message_id, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("from_chat_id", from_chat_id);
            data.put("message_id", message_id);
            data.put("disable_notification", disable_notification);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Forwarded Message", data);
    }

    @Override
    public void onSendingPhoto(ChatId chat_id, InputFile photo, String caption, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("photo", photo);
            data.put("disable_notification", disable_notification);
            data.put("caption", caption);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Photo", data);
    }

    @Override
    public void onSendingAudio(ChatId chat_id, InputFile audio, int duration, String performer, String title, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("audio", audio);
            data.put("disable_notification", disable_notification);
            data.put("duration", duration);
            data.put("performer", performer);
            data.put("title", title);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Audio", data);
    }

    @Override
    public void onSendingDocument(ChatId chat_id, InputFile document, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("document", document);
            data.put("disable_notification", disable_notification);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Document", data);
    }

    @Override
    public void onSendingSticker(ChatId chat_id, InputFile sticker, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("sticker", sticker);
            data.put("disable_notification", disable_notification);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Sticker", data);
    }

    @Override
    public void onSendingVideo(ChatId chat_id, InputFile video, int duration, String caption, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("video", video);
            data.put("disable_notification", disable_notification);
            data.put("duration", duration);
            data.put("caption", caption);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Video", data);
    }

    @Override
    public void onSendingVoice(ChatId chat_id, InputFile voice, int duration, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("voice", voice);
            data.put("disable_notification", disable_notification);
            data.put("duration", duration);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Voice", data);
    }

    @Override
    public void onSendingLocation(ChatId chat_id, float latitude, float longitude, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("latitude", latitude);
            data.put("longitude", longitude);
            data.put("disable_notification", disable_notification);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(response.getChat().getId(), "Sent Location", data);
    }

    @Override
    public void onAnsweringInlineQuery(String inline_query_id, InlineQueryResult[] results, String next_offset, boolean is_personal, int cache_time, boolean response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("inline_query_id", inline_query_id);
            data.put("results", results);
            data.put("next_offset", next_offset);
            data.put("is_personal", is_personal);
            data.put("cache_time", cache_time);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), "Answered Inline Query", data);
    }

    @Override
    public void onSendingChatAction(ChatId chat_id, ChatAction action, boolean response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("action", action);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(getLong(chat_id.getChatId()), "Sent Chat Action", data);
    }

    @Override
    public void onSendingContact(ChatId chat_id, String phone_number, String first_name, String last_name, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("phone_number", phone_number);
            data.put("first_name", first_name);
            data.put("last_name", last_name);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("disable_notification", disable_notification);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(getLong(chat_id.getChatId()), "Sent Contact", data);
    }

    @Override
    public void onSendingVenue(ChatId chat_id, float latitude, float longitude, String title, String address, String foursquare_id, long reply_to_message_id, ReplyMarkup reply_markup, boolean disable_notification, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("latitude", latitude);
            data.put("longitude", longitude);
            data.put("title", title);
            data.put("address", address);
            data.put("foursquare_id", foursquare_id);
            data.put("disable_notification", disable_notification);
            data.put("reply_to_message_id", reply_to_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(getLong(chat_id.getChatId()), "Sent Venue", data);
    }

    private void kickOrUnban(String actionName, ChatId chat_id, long user_id, boolean response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("user_id", user_id);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(getLong(chat_id.getChatId()), actionName, data);
    }

    @Override
    public void onKickChatMember(ChatId chat_id, long user_id, boolean response, IOException e, Date accessTime) {
        kickOrUnban("Kicking Chat Member", chat_id, user_id, response, e, accessTime);
    }

    @Override
    public void onUnbanChatMember(ChatId chat_id, long user_id, boolean response, IOException e, Date accessTime) {
        kickOrUnban("Unban Chat Member", chat_id, user_id, response, e, accessTime);
    }

    @Override
    public void onAnsweringCallbackQuery(String callback_query_id, String text, boolean show_alert, boolean response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("callback_query_id", callback_query_id);
            data.put("text", text);
            data.put("show_alert", show_alert);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), "Answering Callback Query", data);
    }

    @Override
    public void onEditingMessageText(ChatId chat_id, long message_id, String text, ParseMode parse_mode, boolean disable_web_page_preview, InlineKeyboardMarkup reply_markup, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("message_id", message_id);
            data.put("text", text);
            data.put("parse_mode", parse_mode);
            data.put("disable_web_page_preview", disable_web_page_preview);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(getLong(chat_id.getChatId()), "Editing Message Text", data);
    }

    @Override
    public void onEditingMessageText(String inline_message_id, String text, ParseMode parse_mode, boolean disable_web_page_preview, InlineKeyboardMarkup reply_markup, boolean response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("inline_message_id", inline_message_id);
            data.put("text", text);
            data.put("parse_mode", parse_mode);
            data.put("disable_web_page_preview", disable_web_page_preview);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), "Editing Message Text", data);
    }

    @Override
    public void onEditingMessageCaption(ChatId chat_id, long message_id, String caption, InlineKeyboardMarkup reply_markup, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("message_id", message_id);
            data.put("text", caption);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(getLong(chat_id.getChatId()), "Editing Message Caption", data);
    }

    @Override
    public void onEditingMessageCaption(String inline_message_id, String caption, InlineKeyboardMarkup reply_markup, boolean response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("inline_message_id", inline_message_id);
            data.put("text", caption);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), "Editing Message Caption", data);
    }

    @Override
    public void onEditingMessageReplyMarkup(ChatId chat_id, long message_id, InlineKeyboardMarkup reply_markup, Message response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("chat_id", chat_id);
            data.put("message_id", message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(getLong(chat_id.getChatId()), "Editing Reply Markup", data);
    }

    @Override
    public void onEditingMessageReplyMarkup(String inline_message_id, InlineKeyboardMarkup reply_markup, boolean response, IOException e, Date accessTime) {
        Map<String, Object> data = new HashMap<>();
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("inline_message_id", inline_message_id);
            data.put("reply_markup", reply_markup);
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), "Editing Reply Markup", data);
    }

    @Override
    public void onOtherData(String methodName, Map<String, Object> objectMap, Object response, IOException e, Date accessTime) {
        Map<String, Object> data = objectMap;
        if (data == null) {
            data = new HashMap<>();
        }
        if (e != null) {
            data.put("exception", e);
        } else {
            data.put("response", response);
        }
        data.put("accessTime", accessTime);
        trackData(botInfo.getId(), methodName, data);
    }
}
