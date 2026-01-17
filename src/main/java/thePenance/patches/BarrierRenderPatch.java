package thePenance.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import thePenance.powers.BarrierPower;

public class BarrierRenderPatch {

    // 屏障条的颜色 (紫色)
    private static final Color BARRIER_COLOR = new Color(0.7f, 0.2f, 0.9f, 1.0f);
    // 文字颜色 (改成白色，以便在深紫色条上看得清)
    private static final Color TEXT_COLOR = Color.WHITE.cpy();

    // 控制整体高度的偏移量。
    // 如果觉得挡住了原版血条，可以增大这个值往上移；觉得太高了就减小这个值。
    private static final float BAR_Y_OFFSET = 10.0F * Settings.scale;

    // 条的高度
    private static final float BAR_HEIGHT = 12.0F * Settings.scale;

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "renderHealth"
    )
    public static class RenderBarrierBar {
        @SpirePostfixPatch
        public static void PostFix(AbstractCreature __instance, SpriteBatch sb) {
            if (__instance.hasPower(BarrierPower.POWER_ID)) {
                int amt = __instance.getPower(BarrierPower.POWER_ID).amount;
                if (amt > 0) {
                    renderBarrier(__instance, sb, amt);
                }
            }
        }
    }

    private static void renderBarrier(AbstractCreature creature, SpriteBatch sb, int amount) {
        // --- 1. 计算统一的坐标 ---
        // 基于角色碰撞箱的底部 (cY - height/2) 加上偏移量
        float x = creature.hb.cX - creature.hb.width / 2.0F;
        float y = creature.hb.cY - creature.hb.height / 2.0F + BAR_Y_OFFSET;

        // --- 2. 绘制屏障条 ---
        float maxHealth = (float) creature.maxHealth;
        if (maxHealth <= 0) maxHealth = 1;

        float scale = (float) amount / maxHealth;
        if (scale > 1.0F) scale = 1.0F;

        float targetWidth = creature.hb.width * scale;

        sb.setColor(BARRIER_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, targetWidth, BAR_HEIGHT);

        // --- 3. 绘制文字 ---
        // 文字的 X 坐标是角色中心 (hb.cX)
        // 文字的 Y 坐标是 条的Y + 条高度的一半 (为了垂直居中) + 稍微一点微调
        // 注意：renderFontCentered 的 y 坐标是基准线，通常需要加一点点才能视觉居中
        float textY = y + BAR_HEIGHT / 2.0F + (5.0F * Settings.scale);

        FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont,
                String.valueOf(amount), creature.hb.cX, textY, TEXT_COLOR);
    }
}