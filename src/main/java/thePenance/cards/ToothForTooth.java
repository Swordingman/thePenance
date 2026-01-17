package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.WolfCurseHelper;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class ToothForTooth extends BaseCard {
    public static final String ID = makeID("ToothForTooth");
    private static final int COST = 0;
    private static final int HP_LOSS = 2;
    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3;
    private static final int DRAW = 1;
    private static final int UPG_DRAW = 1;

    public ToothForTooth() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(DRAW, UPG_DRAW);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoseHPAction(p, p, HP_LOSS));

        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));

        addToBot(new MakeTempCardInDiscardAction(WolfCurseHelper.getRandomWolfCurse(),1));

        if (p.currentHealth < (p.maxHealth / 2.0F)) {
            addToBot(new DrawCardAction(p, magicNumber));
        }
    }
}