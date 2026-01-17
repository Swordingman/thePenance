package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

public class Silence extends BaseCard {

    public static final String ID = makeID("Silence");

    private static final int COST = 1;
    private static final int BARRIER_AMT = 7;
    private static final int UPGRADE_PLUS_BARRIER = 3;
    private static final int JUDGEMENT_AMT = 2; // 额外裁决数固定为2

    public Silence() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(BARRIER_AMT, UPGRADE_PLUS_BARRIER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得屏障
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, magicNumber), magicNumber));

        // 2. 判定生命值是否低于 50%
        if (p.currentHealth < (p.maxHealth / 2.0F)) {
            addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, JUDGEMENT_AMT), JUDGEMENT_AMT));
        }
    }

    // 保持原来的发光逻辑不变
    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        AbstractPlayer p = AbstractDungeon.player;

        if (p.currentHealth < (p.maxHealth / 2.0F)) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }
}