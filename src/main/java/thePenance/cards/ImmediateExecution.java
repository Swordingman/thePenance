package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.TriggerJudgementAction; // 假设动作在这个包
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

public class ImmediateExecution extends BaseCard {

    public static final String ID = makeID("ImmediateExecution");

    private static final int COST = 1;
    private static final int MAGIC = 3; // 获得裁决数
    private static final int UPGRADE_PLUS_MAGIC = 3;

    public ImmediateExecution() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));

        setMagic(MAGIC, UPGRADE_PLUS_MAGIC);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得裁决
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, magicNumber), magicNumber));

        // 2. 触发裁决伤害
        addToBot(new TriggerJudgementAction(m));
    }
}