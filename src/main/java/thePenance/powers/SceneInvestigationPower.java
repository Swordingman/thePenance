package thePenance.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class SceneInvestigationPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("SceneInvestigationPower");

    private boolean cardsShouldUpgrade;
    private int energyPerTrigger;

    private AbstractCard.CardType lastCardType = null;

    public SceneInvestigationPower(AbstractCreature owner, int stackAmount, int energyAmount, boolean upgrade) {
        super(POWER_ID, PowerType.BUFF, false, owner, stackAmount);

        this.energyPerTrigger = energyAmount;
        this.cardsShouldUpgrade = upgrade;

        updateDescription();
    }

    // 处理堆叠逻辑
    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type != AbstractCard.CardType.ATTACK &&
                card.type != AbstractCard.CardType.SKILL &&
                card.type != AbstractCard.CardType.POWER) {
            lastCardType = null;
            return;
        }

        if (lastCardType == card.type) {
            this.flash();
            triggerEffect(card.type);
        }
        lastCardType = card.type;
    }

    public void updateStats(int newEnergy, boolean newUpgrade) {
        this.energyPerTrigger = Math.max(this.energyPerTrigger, newEnergy);
        this.cardsShouldUpgrade = this.cardsShouldUpgrade || newUpgrade;
        updateDescription();
    }

    private void triggerEffect(AbstractCard.CardType type) {
        // 根据 amount (层数) 循环多次
        for (int i = 0; i < this.amount; i++) {
            switch (type) {
                case ATTACK:
                    giveRandomCard(AbstractCard.CardType.SKILL);
                    break;
                case SKILL:
                    giveRandomCard(AbstractCard.CardType.ATTACK);
                    break;
                case POWER:
                    // 能量 = 基础能量 * 1 (因为外层有循环，所以是加多次)
                    addToBot(new GainEnergyAction(this.energyPerTrigger));
                    break;
            }
        }
    }

    private void giveRandomCard(AbstractCard.CardType typeToGet) {
        // 1. 创建并升级卡牌
        AbstractCard c = AbstractDungeon.returnTrulyRandomCardInCombat(typeToGet).makeCopy();
        if (this.cardsShouldUpgrade) {
            c.upgrade();
        }

        // 2. 修改逻辑属性 (机制)
        c.exhaust = true;
        c.isEthereal = true;

        // 3. 修改文本描述 (视觉)
        // 注意：必须修改 rawDescription 而不是 description
        String exhaustText = com.megacrit.cardcrawl.helpers.GameDictionary.EXHAUST.NAMES[0];
        String etherealText = com.megacrit.cardcrawl.helpers.GameDictionary.ETHEREAL.NAMES[0];

        c.rawDescription += " NL " + exhaustText + " 。 NL " + etherealText + " 。";
        c.initializeDescription();

        // 4. 关键修改：通过自定义 Action 直接将这张特定的卡(c)加入手牌
        // 这样可以避开 MakeTempCardInHandAction 的自动复制机制
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                // 使用特效直接把 c 加入手牌
                // ShowCardAndAddToHandEffect 不会复制卡牌，而是直接使用传入的对象
                AbstractDungeon.effectList.add(new com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect(c));
                this.isDone = true;
            }
        });
    }

    @Override
    public void atStartOfTurn() {
        lastCardType = null;
    }

    @Override
    public void updateDescription() {
        String cardStatus = cardsShouldUpgrade ? "升级后的" : "";

        int totalEnergy = this.amount * this.energyPerTrigger;

        this.description = DESCRIPTIONS[0] + this.amount + " 张" + cardStatus + DESCRIPTIONS[1] +
                this.amount + " 张" + cardStatus + DESCRIPTIONS[2] +
                totalEnergy + DESCRIPTIONS[3];
    }
}