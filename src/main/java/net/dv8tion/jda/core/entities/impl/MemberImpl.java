/*
 *     Copyright 2015-2017 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.core.entities.impl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.apache.http.util.Args;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.*;

public class MemberImpl implements Member
{
    private final Guild guild;
    private final User user;
    private final HashSet<Role> roles = new HashSet<>();
    private final GuildVoiceState voiceState;

    private String nickname;
    private OffsetDateTime joinDate;
    private Game game;
    private OnlineStatus onlineStatus = OnlineStatus.OFFLINE;

    public MemberImpl(Guild guild, User user)
    {
        this.guild = guild;
        this.user = user;
        this.voiceState = new GuildVoiceStateImpl(guild, this);
    }

    @Override
    public User getUser()
    {
        return user;
    }

    @Override
    public Guild getGuild()
    {
        return guild;
    }

    @Override
    public JDA getJDA()
    {
        return user.getJDA();
    }

    @Override
    public OffsetDateTime getJoinDate()
    {
        return joinDate;
    }

    @Override
    public GuildVoiceState getVoiceState()
    {
        return voiceState;
    }

    @Override
    public Game getGame()
    {
        return game;
    }

    @Override
    public OnlineStatus getOnlineStatus()
    {
        return onlineStatus;
    }

    @Override
    public String getNickname()
    {
        return nickname;
    }

    @Override
    public String getEffectiveName()
    {
        return nickname != null ? nickname : user.getName();
    }

    @Override
    public List<Role> getRoles()
    {
        List<Role> roleList = new ArrayList<>(roles);
        roleList.sort(Comparator.reverseOrder());

        return Collections.unmodifiableList(roleList);
    }

    @Override
    public Color getColor()
    {
        for (Role r : getRoles())
        {
            if (r.getColor() != null)
                return r.getColor();
        }
        return null;
    }

    @Override
    public List<Permission> getPermissions()
    {
        return Collections.unmodifiableList(
                Permission.getPermissions(
                        PermissionUtil.getEffectivePermission(guild, this)));
    }

    @Override
    public List<Permission> getPermissions(Channel channel)
    {
        if (!guild.equals(channel.getGuild()))
            throw new IllegalArgumentException("Provided channel is not in the same guild as this member!");

        return Collections.unmodifiableList(
                Permission.getPermissions(
                        PermissionUtil.getEffectivePermission(channel, this)));
    }

    @Override
    public boolean hasPermission(Permission... permissions)
    {
        return PermissionUtil.checkPermission(guild, this, permissions);
    }

    @Override
    public boolean hasPermission(Collection<Permission> permissions)
    {
        Args.notNull(permissions, "Permission Collection");

        return hasPermission(permissions.toArray(new Permission[permissions.size()]));
    }

    @Override
    public boolean hasPermission(Channel channel, Permission... permissions)
    {
        return PermissionUtil.checkPermission(channel, this, permissions);
    }

    @Override
    public boolean hasPermission(Channel channel, Collection<Permission> permissions)
    {
        Args.notNull(permissions, "Permission Collection");

        return hasPermission(channel, permissions.toArray(new Permission[permissions.size()]));
    }

    @Override
    public boolean canInteract(Member member)
    {
        return PermissionUtil.canInteract(this, member);
    }

    @Override
    public boolean canInteract(Role role)
    {
        return PermissionUtil.canInteract(this, role);
    }

    @Override
    public boolean canInteract(Emote emote)
    {
        return PermissionUtil.canInteract(this, emote);
    }

    @Override
    public boolean isOwner() {
        return this.equals(guild.getOwner());
    }

    public MemberImpl setNickname(String nickname)
    {
        this.nickname = nickname;
        return this;
    }

    public MemberImpl setJoinDate(OffsetDateTime joinDate)
    {
        this.joinDate = joinDate;
        return this;
    }

    public MemberImpl setGame(Game game)
    {
        this.game = game;
        return this;
    }

    public MemberImpl setOnlineStatus(OnlineStatus onlineStatus)
    {
        this.onlineStatus = onlineStatus;
        return this;
    }

    public Set<Role> getRoleSet()
    {
        return roles;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Member))
            return false;

        Member oMember = (Member) o;
        return this == oMember || (oMember.getUser().equals(user) && oMember.getGuild().equals(guild));
    }

    @Override
    public int hashCode()
    {
        return (guild.getId() + user.getId()).hashCode();
    }

    @Override
    public String toString()
    {
        return "MB:" + getEffectiveName() + '(' + user.toString() + " / " + guild.toString() +')';
    }

    @Override
    public String getAsMention()
    {
        return user.getAsMention();
    }
}
