package app.template.patches.xmind

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.patch
import app.morphe.patcher.util.returnEarly

/**
 * Unlocks Xmind's locally enforced premium / Pro entitlements.
 *
 * Xmind stores entitlement state in two parallel models:
 *  1. the user's Subscription (rm.x) -> User profile (rm.j0)
 *  2. the TeamProfile (rm.b0)
 * Both feed into im.e3.b(), which returns an EditorEntitlements object
 * (subscribed, premium, collaborate, sheetLimitBypass) consumed by the UI.
 *
 * This patch forces every one of those checks to the "premium" value.
 * Server-side account features (cloud sync, account-tied templates, etc.)
 * are still decided by Xmind's servers and are not affected by this patch.
 */
@Suppress("unused")
val unlockPremiumPatch = patch(
    name = "Unlock Premium / Pro",
    description = "Unlocks Xmind Premium (Pro+) features that are checked locally.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_XMIND)

    execute {
        // --- 1. Subscription model ---------------------------------------
        // Treat every subscription as active, regardless of status/expiry.
        SubscriptionIsActiveFingerprint.method.returnEarly(true)

        // "is Pro" and "is subscribed" on the user profile.
        UserIsProFingerprint.method.returnEarly(true)
        UserIsSubscribedFingerprint.method.returnEarly(true)

        // --- 2. Team profile model ---------------------------------------
        TeamProfileIsActiveFingerprint.method.returnEarly(true)
        TeamProfileIsPremiumFingerprint.method.returnEarly(true)

        // --- 3. Final entitlements object --------------------------------
        // Return EditorEntitlements(subscribed=true, premium=true,
        // collaborate=true, sheetLimitBypass=true) directly.
        EditorEntitlementsFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x1
                new-instance v1, Lgm/a;
                invoke-direct {v1, v0, v0, v0, v0}, Lgm/a;-><init>(ZZZZ)V
                return-object v1
            """.trimIndent(),
        )

        // --- 4. Plan tier ------------------------------------------------
        // Report the Premium tier (gm.h.u) instead of Free/Pro.
        PlanTierFingerprint.method.addInstructions(
            0,
            """
                sget-object v0, Lgm/h;->u:Lgm/h;
                return-object v0
            """.trimIndent(),
        )
    }
}
