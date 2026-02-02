package thePenance.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.patches.CourtRehearsalPatches; // 导入刚才的大文件
import thePenance.util.CardStats;

public class CourtRehearsal extends BaseCard {

    public static final String ID = makeID("CourtRehearsal");
    private static final int COST = 0;

    public CourtRehearsal() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setSelfRetain(true);
        setExhaust(true);

        // 核心：初始化时就打上标记
        CourtRehearsalPatches.CardFields.isRehearsal.set(this, true);
    }

    // 抽到时立刻变身
    @Override
    public void triggerWhenDrawn() {
        super.triggerWhenDrawn();
        CourtRehearsalPatches.transformCard(this);
    }

    // 这张卡实际上几乎不会被“打出”，因为抽到就变了
    // 但为了防止极个别情况（如通过发现卡牌直接生成到手牌且没触发draw），保留基本逻辑
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 如果奇迹般地打出了这张卡本体
        if (this.upgraded) {
            // 回费逻辑已经被Patch接管，但本体这里可以保留双保险，或者留空
        }
    }
}