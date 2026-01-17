package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.WolfCurseHelper;
import thePenance.character.Penance;
import thePenance.util.CardStats;

import java.util.ArrayList;

public class FamilyArbitration extends BaseCard {
    public static final String ID = makeID("FamilyArbitration");
    private static final int COST = 1;
    private static final int DAMAGE = 12;
    private static final int UPG_DAMAGE = 3;

    public FamilyArbitration() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        AbstractCard c = WolfCurseHelper.getRandomWolfCurse();

        addToBot(new MakeTempCardInDiscardAction(c, 1));
    }
}