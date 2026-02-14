package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper; // 必须引入 Helper
import thePenance.patches.PenanceHealPatches;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;

import static thePenance.PenanceMod.makeID;

public class PenanceBasicRelic extends BaseRelic {
    private static final String NAME = "PenanceBasicRelic";
    public static final String ID = makeID(NAME);
    private static final RelicTier TIER = RelicTier.STARTER;
    private static final LandingSound SOUND = LandingSound.MAGICAL;

    public PenanceBasicRelic() {
        super(ID, NAME, Penance.Meta.CARD_COLOR, TIER, SOUND);
        this.counter = 0;
    }

    // 判断是否处于高难度 (Hard 或 Hell)
    private boolean isHardMode() {
        return PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HARD ||
                PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL;
    }

    @Override
    public void atBattleStart() {
        this.flash();
        AbstractPlayer p = AbstractDungeon.player;

        // --- 逻辑分支 1：计算初始屏障比例 ---
        float percentage = 0.10f; // 默认 10%
        if (isHardMode()) {
            percentage = 0.30f;   // 高难度 3
            // 0%
        }

        int startBarrier = (int)(p.maxHealth * percentage);
        if (startBarrier > 0) {
            addToTop(new ApplyPowerAction(p, p, new BarrierPower(p, startBarrier), startBarrier));
        }

        // 2. 获得 1 点裁决 (不变)
        addToTop(new ApplyPowerAction(p, p, new JudgementPower(p, 1), 1));

        // 3. 结算之前存储的治疗量/击杀积攒量
        if (this.counter > 0) {
            addToTop(new ApplyPowerAction(p, p, new BarrierPower(p, this.counter), this.counter));
            this.counter = 0;
            updateDescription();
        }

        addToTop(new RelicAboveCreatureAction(p, this));
    }

    // --- 逻辑分支 2：击杀敌人积攒屏障 (仅限高难度) ---
    @Override
    public void onMonsterDeath(AbstractMonster m) {
        // 如果不是高难度，直接返回，不执行下面的逻辑
        if (!isHardMode()) {
            return;
        }

        // 必须不是爪牙
        if (!m.hasPower(MinionPower.POWER_ID) && !AbstractDungeon.player.isDead) {
            AbstractPlayer p = AbstractDungeon.player;

            // 击杀获得 5% 最大生命值的存储量
            int amount = (int)(p.maxHealth * 0.05f);

            if (amount > 0) {
                this.counter += amount;
                this.flash();
                updateDescription();
            }
        }
    }

    @Override
    public void onVictory() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower(BarrierPower.POWER_ID)) {
            int currentBarrier = p.getPower(BarrierPower.POWER_ID).amount;
            int healAmount = (int)(currentBarrier * 0.10f);

            if (healAmount > 0) {
                this.flash();
                addToBot(new RelicAboveCreatureAction(p, this));
                PenanceHealPatches.forceRealHeal(p, healAmount);

                if (healAmount >= 30 && !p.hasRelic(MedalOfPerseverance.ID)) {
                    AbstractDungeon.getCurrRoom().addRelicToRewards(new MedalOfPerseverance());
                    AbstractDungeon.effectList.add(new SpeechBubble(p.dialogX, p.dialogY, 3.0F, "我...做到了！", true));
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
            updateDescription();
        }
    }

    @Override
    public void onEquip() {
        this.counter = 0;
    }

    private void updateDescription() {
        this.description = getUpdatedDescription();
        if (!this.tips.isEmpty()) {
            this.tips.get(0).body = this.description;
        }
    }

    @Override
    public String getUpdatedDescription() {
        int displayAmount = Math.max(0, this.counter);

        if (isHardMode()) {
            return DESCRIPTIONS[2] + displayAmount + DESCRIPTIONS[1];
        } else {
            return DESCRIPTIONS[0] + displayAmount + DESCRIPTIONS[1];
        }
    }
}