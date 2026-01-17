package thePenance.relics;

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

            // 计算回复量：20%
            int healAmount = (int)(currentBarrier * 0.10f);

            if (healAmount > 0) {
                this.flash();
                // 视觉特效
                addToBot(new RelicAboveCreatureAction(p, this));

                PenanceHealPatches.forceRealHeal(p, healAmount);
            }
        }
    }

    // 这个方法会被下面的 Patch 调用
    public void onTriggerHealing(int amount) {
        AbstractPlayer p = AbstractDungeon.player;

        // 判断当前是否处于战斗中
        boolean inCombat = (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT);

        if (inCombat) {
            // 战斗中：直接转化为屏障
            this.flash();
            addToBot(new RelicAboveCreatureAction(p, this));
            addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, amount), amount));
        } else {
            // 非战斗中（比如篝火、事件）：存储起来
            this.counter += amount;
            this.flash();
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