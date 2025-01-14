package org.jenkinsci.plugins.rolestrategy.casc;

import com.michelin.cio.hudson.plugins.rolestrategy.Role;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Role definition.
 * Used for custom formatting
 * @author Oleg Nenashev
 * @since 2.11
 */
@Restricted(NoExternalUse.class)
public class RoleDefinition {

    private transient Role role;

    @NonNull
    private final String name;
    @CheckForNull
    private final String description;
    @CheckForNull
    private final String pattern;
    private final Set<String> permissions;
    private final Set<String> assignments;

    @DataBoundConstructor
    public RoleDefinition(@NonNull String name, @CheckForNull String description, @CheckForNull String pattern, Collection<String> permissions, Collection<String> assignments) {
        this.name = name;
        this.description = description;
        this.pattern = pattern;
        this.permissions = permissions != null ? new HashSet<>(permissions) : Collections.emptySet();
        this.assignments = assignments != null ? new HashSet<>(assignments) : Collections.emptySet();
        this.role = getRole();
    }

    public final Role getRole() {
        if (role == null) {
            HashSet<String> resolvedIds = new HashSet<>();
            for (String id : permissions) {
                String resolvedId = PermissionFinder.findPermissionId(id);
                if (resolvedId != null) {
                    resolvedIds.add(resolvedId);
                } else {
                    throw new IllegalStateException("Cannot resolve permission for ID: " + id);
                }
            }
            role = new Role(name, pattern, resolvedIds, description);
        }
        return role;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @CheckForNull
    public String getDescription() {
        return description;
    }

    @CheckForNull
    public String getPattern() {
        return pattern;
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public Set<String> getAssignments() {
        return Collections.unmodifiableSet(assignments);
    }

}
