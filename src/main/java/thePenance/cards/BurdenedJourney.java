package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class BurdenedJourney extends BaseCard {

    public static final String ID = makeID("BurdenedJourney");

    private static final int COST = 1;
    private static final int JUDGE_AMT = 3;
    private static final int UPG_JUDGE_AMT = 2; // 3->5

    public BurdenedJourney() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        // 设置魔法数字（裁决）
        setMagic(JUDGE_AMT, UPG_JUDGE_AMT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得裁决
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, magicNumber), magicNumber));

        // 2. 判定生命值 < 50%
        if (p.currentHealth < (p.maxHealth / 2.0F)) {
            // 获得等量的荆棘
            addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, magicNumber), magicNumber));
        }
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        AbstractPlayer p = AbstractDungeon.player;
        if (this.current_x != -1 && this.current_y != -1) {
            if (p.currentHealth < p.maxHealth/2f) {
                this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
            }
        }
    }
}