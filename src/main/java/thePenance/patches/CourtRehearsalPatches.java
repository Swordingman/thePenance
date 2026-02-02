package thePenance.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.cards.CourtRehearsal;
import thePenance.character.Penance;

import java.util.ArrayList;

public class CourtRehearsalPatches {

    // =================================================================
    // 1. 定义 SpireFields
    // =================================================================
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class CardFields {
        public static SpireField<Boolean> isRehearsal = new SpireField<>(() -> false);
        public static SpireField<Boolean> returnEnergy = new SpireField<>(() -> false);
        // 新增：用于存储本体卡牌的对象，用于预览
        public static SpireField<AbstractCard> previewOrigin = new SpireField<>(() -> null);
    }

    // =================================================================
    // 2. 核心变身工具方法
    // =================================================================
    public static void transformCard(AbstractCard oldCard) {
        ArrayList<AbstractCard> pool = new ArrayList<>();
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c.color == Penance.Meta.CARD_COLOR
                    && !c.cardID.equals(CourtRehearsal.ID)
                    && !c.tags.contains(AbstractCard.CardTags.HEALING)
                    && c.type != AbstractCard.CardType.STATUS
                    && c.type != AbstractCard.CardType.CURSE) {
                pool.add(c);
            }
        }

        if (pool.isEmpty()) return;

        AbstractCard newCard = pool.get(AbstractDungeon.cardRandomRng.random(pool.size() - 1)).makeCopy();

        newCard.upgrade();
        newCard.selfRetain = true;
        newCard.exhaust = true;

        // 标记为彩排卡
        CardFields.isRehearsal.set(newCard, true);

        // --- 新增逻辑：设置本体预览 ---
        // 创建一个新的 CourtRehearsal 用于显示
        AbstractCard origin = new CourtRehearsal();
        // 关键：必须把预览卡的 isRehearsal 设为 false，防止预览卡自己又尝试渲染预览卡（虽然逻辑上不太可能，但为了安全）
        CardFields.isRehearsal.set(origin, false);
        // 如果原卡已经升级，预览里的本体也显示为升级版
        if (oldCard.upgraded || (oldCard instanceof CourtRehearsal && oldCard.upgraded)) {
            origin.upgrade();
        }
        CardFields.previewOrigin.set(newCard, origin);
        // ---------------------------

        // 替换手牌
        ArrayList<AbstractCard> hand = AbstractDungeon.player.hand.group;
        int index = hand.indexOf(oldCard);
        if (index != -1) {
            hand.set(index, newCard);
            newCard.current_x = oldCard.current_x;
            newCard.current_y = oldCard.current_y;
            newCard.target_x = oldCard.target_x;
            newCard.target_y = oldCard.target_y;
            newCard.drawScale = oldCard.drawScale;
            newCard.targetDrawScale = oldCard.targetDrawScale;
            newCard.angle = oldCard.angle;
            newCard.targetAngle = oldCard.targetAngle;
            newCard.superFlash();
            newCard.applyPowers();
        }
    }

    // =================================================================
    // 3. 渲染 Patch：始终在右上角渲染本体预览
    // =================================================================
    @SpirePatch(clz = AbstractCard.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class RenderAlwaysPreview {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            // 校验：是彩排卡、没被隐藏、且不在卡牌单一视图(SingleCardView)中
            if (CardFields.isRehearsal.get(__instance)
                    && !Settings.hideCards
                    && !__instance.isFlipped
                    && !__instance.isLocked) {

                AbstractCard preview = CardFields.previewOrigin.get(__instance);

                if (preview != null) {
                    // --- 1. 同步状态 ---
                    // 让预览卡的透明度跟主卡保持一致（比如主卡正在淡出）
                    preview.transparency = __instance.transparency;
                    // 让预览卡的角度跟主卡一致（手牌实际上是扇形展开的）
                    preview.angle = __instance.angle;

                    // --- 2. 计算位置 ---
                    // 我们希望它一直悬浮在右上角。
                    // 这里的 0.8F 和 0.8F 是偏移量系数，你可以根据实际视觉效果微调
                    // 如果觉得挡住了旁边的牌，可以把 xOffset 改小一点，或者把 drawScale 改小
                    float xOffset = (AbstractCard.IMG_WIDTH / 2.0F * 0.85F);
                    float yOffset = (AbstractCard.IMG_HEIGHT / 2.0F * 0.9F);

                    // 旋转向量计算（因为手牌是歪的，直接加坐标会错位，需要根据角度旋转偏移量）
                    // 简单的数学：x' = x*cos - y*sin, y' = x*sin + y*cos
                    // 但在这里我们只需要相对于卡牌中心的偏移，Spire已有的 render 逻辑比较复杂
                    // 我们直接利用 current_x/y 加上经过缩放和旋转处理的偏移

                    // 为了简化计算并保证跟随准确，我们不手动算三角函数，
                    // 而是直接设定位置，让 preview.render 自己去处理部分逻辑，
                    // 但 preview.render 会依赖它自己的 current_x/y。

                    // 简易方案：不考虑极致的角度偏移，直接按缩放跟随
                    // (如果手牌角度很大，这个简易方案可能会导致小卡位置稍微偏一点，但在手牌中通常没问题)
                    preview.current_x = __instance.current_x + (xOffset * __instance.drawScale);
                    preview.current_y = __instance.current_y + (yOffset * __instance.drawScale);

                    // --- 3. 设置大小 ---
                    // 设定为本体的 0.4 倍大小（做成一个小徽章的感觉）
                    // 太大了会挡住后面那张手牌
                    preview.drawScale = __instance.drawScale * 0.4F;

                    // --- 4. 渲染 ---
                    preview.render(sb);
                }
            }
        }
    }

    // =================================================================
    // 4. 下面保持不变
    // =================================================================
    @SpirePatch(clz = AbstractPlayer.class, method = "applyStartOfTurnCards")
    public static class TurnStartTransform {
        @SpirePrefixPatch
        public static void Prefix(AbstractPlayer __instance) {
            ArrayList<AbstractCard> handCopy = new ArrayList<>(__instance.hand.group);
            for (AbstractCard c : handCopy) {
                if (CardFields.isRehearsal.get(c)) {
                    transformCard(c);
                }
            }
        }
    }
}