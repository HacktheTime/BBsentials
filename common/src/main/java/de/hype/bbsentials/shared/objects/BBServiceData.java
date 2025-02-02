package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.server.objects.BBUser;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.constants.TradeType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BBServiceData {
    protected int serviceId;
    protected StatusConstants status = StatusConstants.OPEN;
    protected String description;
    protected TradeType type;
    protected String title;
    protected BBUser hoster;
    protected List<Helper> helpers;
    protected List<Participiant> participants = new ArrayList<>();
    protected List<Participiant> queue = new ArrayList<>();
    protected int price;
    protected String dcMessageID;
    protected int maxUsers;
    protected boolean joinLock;
    protected boolean forceModOnline;
    public boolean circulateParticipants;

    public BBServiceData(String description, BBUser hoster, int price, List<Helper> helpers, int maxUsers, boolean forceModOnline) {
        this(null, description, hoster, price, helpers, maxUsers, forceModOnline);
    }

    public BBServiceData(@NotNull TradeType type, BBUser hoster, int price, List<Helper> helpers, boolean forceModOnline) {
        this(type, type.getDescription(), hoster, price, helpers, type.getMaximumUsers(helpers.size()), forceModOnline);
    }

    public BBServiceData(TradeType type, String description, BBUser hoster, int price, List<Helper> helpers, int maxUsers, boolean forceModOnline) {
        this.type = type;
        this.hoster = hoster;
        this.helpers = helpers;
        this.price = price;
        this.maxUsers = maxUsers;
        this.forceModOnline = forceModOnline;
        this.description = description;
    }

    public int getServiceId() {
        return serviceId;
    }

    public static class Participiant {
        public BBUser user;
        public boolean priority;
        public boolean autoRequeue;
        public Instant joinTime;
        public int price;

        public Participiant(BBUser user, boolean priority, boolean free, BBServiceData data) {
            this(user, priority, free, Instant.now(), false, data);
        }

        public Participiant(BBUser user, boolean priority, boolean free, Instant joinTime, boolean autoRequeue, BBServiceData data) {
            this(user, priority, joinTime, autoRequeue, free ? 0 : data.price);
        }

        public Participiant(BBUser user, boolean priority, Instant joinTime, boolean autoRequeue, int price) {
            this.user = user;
            this.priority = priority;
            this.price = price;
            this.autoRequeue = autoRequeue;
            this.joinTime = joinTime;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Participiant participiant) return participiant.user.equals(user);
            if (obj instanceof BBUser bbUser) return bbUser.equals(this.user);
            return false;
        }

        @Override
        public int hashCode() {
            return user.hashCode();
        }

        public String getDiscordParticipiantString() {
            String string = user.getMcUsername();
            if (price == 0) string = "**%s**".formatted(string);
            if (priority) string = "__%s__".formatted(string);
            if (autoRequeue) string = "*%s*".formatted(string);
            return string;

        }
    }

    public static class Helper {
        public BBUser user;
        public String username;

        public Helper(BBUser user) {
            this.user = user;
            this.username = user.getMcUsername();
        }

        public Helper(BBUser user, String username) {
            this.user = user;
            this.username = username;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Helper helper) return helper.username.equals(username);
            if (obj instanceof String string) {
                if (username != null) return string.equalsIgnoreCase(username);
                else return user.getMcUsername().equalsIgnoreCase(string);
            }
            if (obj instanceof BBUser bbUser) {
                if (user != null) return bbUser.equals(user);
                else return bbUser.getMcUsername().equalsIgnoreCase(username);
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (username != null) return username.hashCode();
            else return user.getMcUsername().hashCode();
        }
    }
}
