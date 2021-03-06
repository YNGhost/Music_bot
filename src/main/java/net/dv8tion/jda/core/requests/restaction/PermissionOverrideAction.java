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

package net.dv8tion.jda.core.requests.restaction;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.PermissionOverrideImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.entities.impl.VoiceChannelImpl;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import org.apache.http.util.Args;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Extension of {@link net.dv8tion.jda.core.requests.RestAction RestAction} specifically
 * designed to create a {@link net.dv8tion.jda.core.entities.PermissionOverride PermissionOverride}
 * for a {@link net.dv8tion.jda.core.entities.Channel Channel}.
 * This extension allows setting properties before executing the action.
 *
 * @since  3.0
 * @author Florian Spieß
 */
public class PermissionOverrideAction extends RestAction<PermissionOverride>
{

    private long allow = 0;
    private long deny = 0;
    private final Channel channel;

    private final Member member;
    private final Role role;

    /**
     * Creates a new PermissionOverrideAction instance
     *
     * @param api
     *        The current JDA instance
     * @param route
     *        The {@link net.dv8tion.jda.core.requests.Route.CompiledRoute Route.CompiledRoute} to be used for rate limit handling
     * @param channel
     *        The target {@link net.dv8tion.jda.core.entities.Channel Channel} for the PermissionOverride
     * @param member
     *        The target {@link net.dv8tion.jda.core.entities.Member Member} that will be affected by the PermissionOverride
     */
    public PermissionOverrideAction(JDA api, Route.CompiledRoute route, Channel channel, Member member)
    {
        super(api, route, null);
        this.channel = channel;
        this.member = member;
        this.role = null;
    }

    /**
     * Creates a new PermissionOverrideAction instance
     *
     * @param api
     *        The current JDA instance
     * @param route
     *        The {@link net.dv8tion.jda.core.requests.Route.CompiledRoute Route.CompiledRoute} to be used for rate limit handling
     * @param channel
     *        The target {@link net.dv8tion.jda.core.entities.Channel Channel} for the PermissionOverride
     * @param role
     *        The target {@link net.dv8tion.jda.core.entities.Role Role} that will be affected by the PermissionOverride
     */
    public PermissionOverrideAction(JDA api, Route.CompiledRoute route, Channel channel, Role role)
    {
        super(api, route, null);
        this.channel = channel;
        this.member = null;
        this.role = role;
    }


    /**
     * The currently set of allowed permission bits.
     * <br>This value represents all <b>granted</b> permissions
     * in the raw bitwise representation.
     *
     * <p>Use {@link #getAllowedPermissions()} to retrieve a {@link java.util.List List}
     * with {@link net.dv8tion.jda.core.Permission Permissions} for this value
     *
     * @return long value of granted permissions
     */
    public long getAllow()
    {
        return allow;
    }

    /**
     * Immutable list of {@link net.dv8tion.jda.core.Permission Permissions}
     * that would be <b>granted</b> by the PermissionOverride that is created by this action.
     *
     * @return immutable list of granted {@link net.dv8tion.jda.core.Permission Permissions}
     */
    public List<Permission> getAllowedPermissions()
    {
        return Collections.unmodifiableList(Permission.getPermissions(allow));
    }


    /**
     * The currently set of denied permission bits.
     * <br>This value represents all <b>denied</b> permissions
     * in the raw bitwise representation.
     *
     * <p>Use {@link #getDeniedPermissions()} to retrieve a {@link java.util.List List}
     * with {@link net.dv8tion.jda.core.Permission Permissions} for this value
     *
     * @return long value of denied permissions
     */
    public long getDeny()
    {
        return deny;
    }

    /**
     * Immutable list of {@link net.dv8tion.jda.core.Permission Permissions}
     * that would be <b>denied</b> by the PermissionOverride that is created by this action.
     *
     * @return immutable list of denied {@link net.dv8tion.jda.core.Permission Permissions}
     */
    public List<Permission> getDeniedPermissions()
    {
        return Collections.unmodifiableList(Permission.getPermissions(deny));
    }


    /**
     * The currently set of inherited permission bits.
     * <br>This value represents all permissions that are not explicitly allowed or denied
     * in their raw bitwise representation.
     * <br>Inherited Permissions are permissions that are defined by other rules
     * from maybe other PermissionOverrides or a Role.
     *
     * <p>Use {@link #getInheritedPermissions()} to retrieve a {@link java.util.List List}
     * with {@link net.dv8tion.jda.core.Permission Permissions} for this value
     *
     * @return long value of inherited permissions
     */
    public long getInherited()
    {
        return ~allow & ~deny;
    }

    /**
     * Immutable list of {@link net.dv8tion.jda.core.Permission Permissions}
     * that would be <b>inherited</b> from other permission holders.
     * <br>Permissions returned are not explicitly granted or denied!
     *
     * @return immutable list of inherited {@link net.dv8tion.jda.core.Permission Permissions}
     *
     * @see    #getInherited()
     */
    public List<Permission> getInheritedPermissions()
    {
        return Permission.getPermissions(getInherited());
    }


    /**
     * Whether this Action will
     * create a {@link net.dv8tion.jda.core.entities.PermissionOverride PermissionOverride}
     * for a {@link net.dv8tion.jda.core.entities.Member Member} or not
     *
     * @return True, if this is targeting a Member
     *         If this is {@code false} it is targeting a {@link net.dv8tion.jda.core.entities.Role Role}. ({@link #isRole()})
     */
    public boolean isMember()
    {
        return member != null;
    }

    /**
     * Whether this Action will
     * create a {@link net.dv8tion.jda.core.entities.PermissionOverride PermissionOverride}
     * for a {@link net.dv8tion.jda.core.entities.Role Role} or not
     *
     * @return True, if this is targeting a Role.
     *         If this is {@code false} it is targeting a {@link net.dv8tion.jda.core.entities.Member Member}. ({@link #isMember()})
     */
    public boolean isRole()
    {
        return role != null;
    }


    /**
     * Sets the value of explicitly granted permissions
     * using the bitwise representation of a set of {@link net.dv8tion.jda.core.Permission Permissions}.
     * <br>This value can be retrieved through {@link net.dv8tion.jda.core.Permission#getRaw(net.dv8tion.jda.core.Permission...) Permissions.getRaw(Permission...)}!
     * <br><b>Note: Permissions not marked as {@link net.dv8tion.jda.core.Permission#isChannel() isChannel()} will have no affect!</b>
     *
     * @param  allowBits
     *         The <b>positive</b> bits representing the granted
     *         permissions for the new PermissionOverride
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided bits are negative
     *         or higher than {@link net.dv8tion.jda.core.Permission#ALL_PERMISSIONS Permission.ALL_PERMISSIONS}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setAllow(long allowBits)
    {
        Args.notNegative(allowBits, "Granted permissions value");
        Args.check(allowBits <= Permission.ALL_PERMISSIONS, "Specified allow value may not be greater than a full permission set");
        this.allow = allowBits;
        return this;
    }

    /**
     * Sets the value of explicitly granted permissions
     * using a Collection of {@link net.dv8tion.jda.core.Permission Permissions}.
     * <br><b>Note: Permissions not marked as {@link net.dv8tion.jda.core.Permission#isChannel() isChannel()} will have no affect!</b>
     *
     * @param  permissions
     *         The Collection of Permissions representing the granted
     *         permissions for the new PermissionOverride.
     *         <br>If the provided value is {@code null} the permissions are reset to the default of none
     *
     * @throws java.lang.IllegalArgumentException
     *         If the any of the specified Permissions is {@code null}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setAllow(Collection<Permission> permissions)
    {
        if (permissions == null || permissions.isEmpty())
            return setAllow(0);
        checkNull(permissions, "Permission");
        return setAllow(Permission.getRaw(permissions));
    }

    /**
     * Sets the value of explicitly granted permissions
     * using a set of {@link net.dv8tion.jda.core.Permission Permissions}.
     * <br><b>Note: Permissions not marked as {@link net.dv8tion.jda.core.Permission#isChannel() isChannel()} will have no affect!</b>
     *
     * @param  permissions
     *         The Permissions representing the granted
     *         permissions for the new PermissionOverride.
     *         <br>If the provided value is {@code null} the permissions are reset to the default of none
     *
     * @throws java.lang.IllegalArgumentException
     *         If the any of the specified Permissions is {@code null}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setAllow(Permission... permissions)
    {
        if (permissions == null || permissions.length < 1)
            return setAllow(0);
        checkNull(permissions, "Permission");
        return setAllow(Permission.getRaw(permissions));
    }


    /**
     * Sets the value of explicitly denied permissions
     * using the bitwise representation of a set of {@link net.dv8tion.jda.core.Permission Permissions}.
     * <br>This value can be retrieved through {@link net.dv8tion.jda.core.Permission#getRaw(net.dv8tion.jda.core.Permission...) Permissions.getRaw(Permission...)}!
     * <br><b>Note: Permissions not marked as {@link net.dv8tion.jda.core.Permission#isChannel() isChannel()} will have no affect!</b>
     *
     * @param  denyBits
     *         The <b>positive</b> bits representing the denied
     *         permissions for the new PermissionOverride
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided bits are negative
     *         or higher than {@link net.dv8tion.jda.core.Permission#ALL_PERMISSIONS Permission.ALL_PERMISSIONS}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setDeny(long denyBits)
    {
        Args.notNegative(denyBits, "Denied permissions value");
        Args.check(denyBits <= Permission.ALL_PERMISSIONS, "Specified allow value may not be greater than a full permission set");
        this.deny = denyBits;
        return this;
    }

    /**
     * Sets the value of explicitly denied permissions
     * using a Collection of {@link net.dv8tion.jda.core.Permission Permissions}.
     * <br><b>Note: Permissions not marked as {@link net.dv8tion.jda.core.Permission#isChannel() isChannel()} will have no affect!</b>
     *
     * @param  permissions
     *         The Collection of Permissions representing the denied
     *         permissions for the new PermissionOverride.
     *         <br>If the provided value is {@code null} the permissions are reset to the default of none
     *
     * @throws java.lang.IllegalArgumentException
     *         If the any of the specified Permissions is {@code null}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setDeny(Collection<Permission> permissions)
    {
        if (permissions == null || permissions.isEmpty())
            return setDeny(0);
        checkNull(permissions, "Permission");
        return setDeny(Permission.getRaw(permissions));
    }

    /**
     * Sets the value of explicitly denied permissions
     * using a set of {@link net.dv8tion.jda.core.Permission Permissions}.
     * <br><b>Note: Permissions not marked as {@link net.dv8tion.jda.core.Permission#isChannel() isChannel()} will have no affect!</b>
     *
     * @param  permissions
     *         The Permissions representing the denied
     *         permissions for the new PermissionOverride.
     *         <br>If the provided value is {@code null} the permissions are reset to the default of none
     *
     * @throws java.lang.IllegalArgumentException
     *         If the any of the specified Permissions is {@code null}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setDeny(Permission... permissions)
    {
        if (permissions == null || permissions.length < 1)
            return setDeny(0);
        checkNull(permissions, "Permission");
        return setDeny(Permission.getRaw(permissions));
    }


    /**
     * Combination of {@link #setAllow(long)} and {@link #setDeny(long)}
     *
     * @param  allowBits
     *         A non-negative bitwise representation
     *         of granted Permissions
     * @param  denyBits
     *         A non-negative bitwise representation
     *         of denied Permissions
     *
     * @throws java.lang.IllegalArgumentException
     *         If any of the provided bits are negative
     *         or higher than {@link net.dv8tion.jda.core.Permission#ALL_PERMISSIONS Permission.ALL_PERMISSIONS}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setPermissions(long allowBits, long denyBits)
    {
        setAllow(allowBits);
        setDeny(denyBits);
        return this;
    }

    /**
     * Combination of {@link #setAllow(java.util.Collection)} and {@link #setDeny(java.util.Collection)}
     * <br>If a passed collection is {@code null} it resets the represented value to {@code 0} - no permission specifics.
     *
     * @param  grantPermissions
     *         A Collection of {@link net.dv8tion.jda.core.Permission Permissions}
     *         representing all explicitly granted Permissions for the PermissionOverride
     * @param  denyPermissions
     *         A Collection of {@link net.dv8tion.jda.core.Permission Permissions}
     *         representing all explicitly denied Permissions for the PermissionOverride
     *
     * @throws java.lang.IllegalArgumentException
     *         If the any of the specified Permissions is {@code null}
     *
     * @return The current PermissionOverrideAction - for chaining convenience
     */
    public PermissionOverrideAction setPermissions(Collection<Permission> grantPermissions, Collection<Permission> denyPermissions)
    {
        setAllow(grantPermissions);
        setDeny(denyPermissions);
        return this;
    }


    @Override
    protected void finalizeData()
    {
        JSONObject object = new JSONObject();
        object.put("type", isRole() ? "role" : "member");
        object.put("allow", allow);
        object.put("deny", deny);

        super.data = object;
        super.finalizeData();
    }

    @Override
    protected void handleResponse(Response response, Request request)
    {
        if (!response.isOk())
        {
            request.onFailure(response);
            return;
        }

        JSONObject object = response.getObject();
        PermissionOverrideImpl override = new PermissionOverrideImpl(channel, member, role).setAllow(allow).setDeny(deny);

        boolean isText = channel instanceof TextChannelImpl;

        if (isMember())
        {
            if (isText)
                ((TextChannelImpl) channel).getMemberOverrideMap().put(member, override);
            else
                ((VoiceChannelImpl) channel).getMemberOverrideMap().put(member, override);
        }
        else
        {
            if (isText)
                ((TextChannelImpl) channel).getRoleOverrideMap().put(role, override);
            else
                ((VoiceChannelImpl) channel).getRoleOverrideMap().put(role, override);
        }

        request.onSuccess(override);
    }

    private void checkNull(Collection<?> collection, String name)
    {
        Args.notNull(collection, name);
        collection.forEach(e -> Args.notNull(e, name));
    }

    private <T> void checkNull(T[] arr, String name)
    {
        Args.notNull(arr, name);
        for (T e : arr)
            Args.notNull(e, name);
    }

}
