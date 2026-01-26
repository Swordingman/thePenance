package thePenance.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import thePenance.character.Penance;
import thePenance.patches.PenanceHealPatches;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;

import static thePenance.PenanceMod.makeID;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
// 记得引入你新写的奖杯遗物
import thePenance.relics.MedalOfPerseverance;

public class PenanceBasicRelic extends BaseRelic {
    private static final String NAME = "PenanceBasicRelic"; // 对应图片文件名
    public static final String ID = makeID(NAME);
    private static final RelicTier TIER = RelicTier.STARTER;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public PenanceBasicRelic() {
        super(ID, NAME, Penance.Meta.CARD_COLOR, TIER, SOUND);
        this.counter = 0; // 初始化存储的治疗量为 0
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;

        // 1. 获得 10% 最大生命值的屏障
        int maxHpBarrier = (int)(p.maxHealth * 0.10f);
        if (maxHpBarrier > 0) {
            addToTop(new ApplyPowerAction(p, p, new BarrierPower(p, maxHpBarrier), maxHpBarrier));
        }

        // 2. 获得 1 点裁决
        addToTop(new ApplyPowerAction(p, p, new JudgementPower(p, 1), 1));

        // 3. 结算之前存储的治疗量 (counter)
        if (this.counter > 0) {
            addToTop(new ApplyPowerAction(p, p, new BarrierPower(p, this.counter), this.counter));
            // 结算后清零，因为这些屏障这局打完就没了，不能留到下下一局
            this.counter = 0;

            this.description = getUpdatedDescription();
            if (!this.tips.isEmpty()) {
                this.tips.get(0).body = this.description;
            }
        }

        addToTop(new RelicAboveCreatureAction(p, this));
    }

    // --- 新增：战斗胜利时的逻辑 ---
    @Override
    public void onVictory() {
        AbstractPlayer p = AbstractDungeon.player;

        // 检查是否有屏障
        if (p.hasPower(BarrierPower.POWER_ID)) {
            int currentBarrier = p.getPower(BarrierPower.POWER_ID).amount;

            // 计算回复量：10%
            // 注意：Java整数除法会向下取整。300 * 0.1 = 30，满足条件。
            // 但如果是 299 * 0.1 = 29.9 -> 29，则不满足。
            int healAmount = (int)(currentBarrier * 0.10f);

            if (healAmount > 0) {
                this.flash();
                addToBot(new RelicAboveCreatureAction(p, this));

                // 执行真正的治疗
                PenanceHealPatches.forceRealHeal(p, healAmount);

                // --- 毅力奖杯判定逻辑 (修复版) ---
                // 必须检查 healAmount 是否 >= 30
                if (healAmount >= 30 && !p.hasRelic(MedalOfPerseverance.ID)) {

                    // 【重要修改】不要使用 addToBot，直接执行！
                    // 这样能确保在奖励界面生成前，遗物就已经进去了。
                    AbstractDungeon.getCurrRoom().addRelicToRewards(new MedalOfPerseverance());

                    // 气泡特效是视觉效果，可以直接加到 effectList，或者用 Action 都可以
                    // 这里直接加到 effectList 响应最快
                    AbstractDungeon.effectList.add(new SpeechBubble(
                            p.dialogX,
                            p.dialogY,
                            3.0F, "我...做到了！", true));
                }
            }
        }
    }

    public void onTriggerHealing(int amount) {
        AbstractPlayer p = AbstractDungeon.player;
        boolean inCombat = (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT);

        if (inCombat) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, amount), amount));
        } else {
            this.counter += amount;
            this.flash();

            this.description = getUpdatedDescription();

            if (!this.tips.isEmpty()) {
                this.tips.get(0).body = this.description;
            }
        }
    }

    @Override
    public void onEquip() {
        this.counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        // 如果 counter 是 -1（默认值），就显示为 0；否则显示实际数值
        int displayAmount = Math.max(0, this.counter);
        return DESCRIPTIONS[0] + displayAmount + DESCRIPTIONS[1];
    }
}