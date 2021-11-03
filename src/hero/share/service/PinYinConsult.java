// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.Iterator;
import java.util.Set;
import java.util.LinkedHashMap;

public class PinYinConsult {

    private static PinYinConsult instance;
    private static LinkedHashMap<String, Integer> spellMap;

    static {
        PinYinConsult.spellMap = new LinkedHashMap<String, Integer>(400);
    }

    public static PinYinConsult getInstance() {
        if (PinYinConsult.instance == null) {
            PinYinConsult.instance = new PinYinConsult();
        }
        return PinYinConsult.instance;
    }

    private PinYinConsult() {
        this.initSpell();
    }

    private void initSpell() {
        PinYinConsult.spellMap.put("a", -20319);
        PinYinConsult.spellMap.put("ai", -20317);
        PinYinConsult.spellMap.put("an", -20304);
        PinYinConsult.spellMap.put("ang", -20295);
        PinYinConsult.spellMap.put("ao", -20292);
        PinYinConsult.spellMap.put("ba", -20283);
        PinYinConsult.spellMap.put("bai", -20265);
        PinYinConsult.spellMap.put("ban", -20257);
        PinYinConsult.spellMap.put("bang", -20242);
        PinYinConsult.spellMap.put("bao", -20230);
        PinYinConsult.spellMap.put("bei", -20051);
        PinYinConsult.spellMap.put("ben", -20036);
        PinYinConsult.spellMap.put("beng", -20032);
        PinYinConsult.spellMap.put("bi", -20026);
        PinYinConsult.spellMap.put("bian", -20002);
        PinYinConsult.spellMap.put("biao", -19990);
        PinYinConsult.spellMap.put("bie", -19986);
        PinYinConsult.spellMap.put("bin", -19982);
        PinYinConsult.spellMap.put("bing", -19976);
        PinYinConsult.spellMap.put("bo", -19805);
        PinYinConsult.spellMap.put("bu", -19784);
        PinYinConsult.spellMap.put("ca", -19775);
        PinYinConsult.spellMap.put("cai", -19774);
        PinYinConsult.spellMap.put("can", -19763);
        PinYinConsult.spellMap.put("cang", -19756);
        PinYinConsult.spellMap.put("cao", -19751);
        PinYinConsult.spellMap.put("ce", -19746);
        PinYinConsult.spellMap.put("ceng", -19741);
        PinYinConsult.spellMap.put("cha", -19739);
        PinYinConsult.spellMap.put("chai", -19728);
        PinYinConsult.spellMap.put("chan", -19725);
        PinYinConsult.spellMap.put("chang", -19715);
        PinYinConsult.spellMap.put("chao", -19540);
        PinYinConsult.spellMap.put("che", -19531);
        PinYinConsult.spellMap.put("chen", -19525);
        PinYinConsult.spellMap.put("cheng", -19515);
        PinYinConsult.spellMap.put("chi", -19500);
        PinYinConsult.spellMap.put("chong", -19484);
        PinYinConsult.spellMap.put("chou", -19479);
        PinYinConsult.spellMap.put("chu", -19467);
        PinYinConsult.spellMap.put("chuai", -19289);
        PinYinConsult.spellMap.put("chuan", -19288);
        PinYinConsult.spellMap.put("chuang", -19281);
        PinYinConsult.spellMap.put("chui", -19275);
        PinYinConsult.spellMap.put("chun", -19270);
        PinYinConsult.spellMap.put("chuo", -19263);
        PinYinConsult.spellMap.put("ci", -19261);
        PinYinConsult.spellMap.put("cong", -19249);
        PinYinConsult.spellMap.put("cou", -19243);
        PinYinConsult.spellMap.put("cu", -19242);
        PinYinConsult.spellMap.put("cuan", -19238);
        PinYinConsult.spellMap.put("cui", -19235);
        PinYinConsult.spellMap.put("cun", -19227);
        PinYinConsult.spellMap.put("cuo", -19224);
        PinYinConsult.spellMap.put("da", -19218);
        PinYinConsult.spellMap.put("dai", -19212);
        PinYinConsult.spellMap.put("dan", -19038);
        PinYinConsult.spellMap.put("dang", -19023);
        PinYinConsult.spellMap.put("dao", -19018);
        PinYinConsult.spellMap.put("de", -19006);
        PinYinConsult.spellMap.put("deng", -19003);
        PinYinConsult.spellMap.put("di", -18996);
        PinYinConsult.spellMap.put("dian", -18977);
        PinYinConsult.spellMap.put("diao", -18961);
        PinYinConsult.spellMap.put("die", -18952);
        PinYinConsult.spellMap.put("ding", -18783);
        PinYinConsult.spellMap.put("diu", -18774);
        PinYinConsult.spellMap.put("dong", -18773);
        PinYinConsult.spellMap.put("dou", -18763);
        PinYinConsult.spellMap.put("du", -18756);
        PinYinConsult.spellMap.put("duan", -18741);
        PinYinConsult.spellMap.put("dui", -18735);
        PinYinConsult.spellMap.put("dun", -18731);
        PinYinConsult.spellMap.put("duo", -18722);
        PinYinConsult.spellMap.put("e", -18710);
        PinYinConsult.spellMap.put("en", -18697);
        PinYinConsult.spellMap.put("er", -18696);
        PinYinConsult.spellMap.put("fa", -18526);
        PinYinConsult.spellMap.put("fan", -18518);
        PinYinConsult.spellMap.put("fang", -18501);
        PinYinConsult.spellMap.put("fei", -18490);
        PinYinConsult.spellMap.put("fen", -18478);
        PinYinConsult.spellMap.put("feng", -18463);
        PinYinConsult.spellMap.put("fo", -18448);
        PinYinConsult.spellMap.put("fou", -18447);
        PinYinConsult.spellMap.put("fu", -18446);
        PinYinConsult.spellMap.put("ga", -18239);
        PinYinConsult.spellMap.put("gai", -18237);
        PinYinConsult.spellMap.put("gan", -18231);
        PinYinConsult.spellMap.put("gang", -18220);
        PinYinConsult.spellMap.put("gao", -18211);
        PinYinConsult.spellMap.put("ge", -18201);
        PinYinConsult.spellMap.put("gei", -18184);
        PinYinConsult.spellMap.put("gen", -18183);
        PinYinConsult.spellMap.put("geng", -18181);
        PinYinConsult.spellMap.put("gong", -18012);
        PinYinConsult.spellMap.put("gou", -17997);
        PinYinConsult.spellMap.put("gu", -17988);
        PinYinConsult.spellMap.put("gua", -17970);
        PinYinConsult.spellMap.put("guai", -17964);
        PinYinConsult.spellMap.put("guan", -17961);
        PinYinConsult.spellMap.put("guang", -17950);
        PinYinConsult.spellMap.put("gui", -17947);
        PinYinConsult.spellMap.put("gun", -17931);
        PinYinConsult.spellMap.put("guo", -17928);
        PinYinConsult.spellMap.put("ha", -17922);
        PinYinConsult.spellMap.put("hai", -17759);
        PinYinConsult.spellMap.put("han", -17752);
        PinYinConsult.spellMap.put("hang", -17733);
        PinYinConsult.spellMap.put("hao", -17730);
        PinYinConsult.spellMap.put("he", -17721);
        PinYinConsult.spellMap.put("hei", -17703);
        PinYinConsult.spellMap.put("hen", -17701);
        PinYinConsult.spellMap.put("heng", -17697);
        PinYinConsult.spellMap.put("hong", -17692);
        PinYinConsult.spellMap.put("hou", -17683);
        PinYinConsult.spellMap.put("hu", -17676);
        PinYinConsult.spellMap.put("hua", -17496);
        PinYinConsult.spellMap.put("huai", -17487);
        PinYinConsult.spellMap.put("huan", -17482);
        PinYinConsult.spellMap.put("huang", -17468);
        PinYinConsult.spellMap.put("hui", -17454);
        PinYinConsult.spellMap.put("hun", -17433);
        PinYinConsult.spellMap.put("huo", -17427);
        PinYinConsult.spellMap.put("ji", -17417);
        PinYinConsult.spellMap.put("jia", -17202);
        PinYinConsult.spellMap.put("jian", -17185);
        PinYinConsult.spellMap.put("jiang", -16983);
        PinYinConsult.spellMap.put("jiao", -16970);
        PinYinConsult.spellMap.put("jie", -16942);
        PinYinConsult.spellMap.put("jin", -16915);
        PinYinConsult.spellMap.put("jing", -16733);
        PinYinConsult.spellMap.put("jiong", -16708);
        PinYinConsult.spellMap.put("jiu", -16706);
        PinYinConsult.spellMap.put("ju", -16689);
        PinYinConsult.spellMap.put("juan", -16664);
        PinYinConsult.spellMap.put("jue", -16657);
        PinYinConsult.spellMap.put("jun", -16647);
        PinYinConsult.spellMap.put("ka", -16474);
        PinYinConsult.spellMap.put("kai", -16470);
        PinYinConsult.spellMap.put("kan", -16465);
        PinYinConsult.spellMap.put("kang", -16459);
        PinYinConsult.spellMap.put("kao", -16452);
        PinYinConsult.spellMap.put("ke", -16448);
        PinYinConsult.spellMap.put("ken", -16433);
        PinYinConsult.spellMap.put("keng", -16429);
        PinYinConsult.spellMap.put("kong", -16427);
        PinYinConsult.spellMap.put("kou", -16423);
        PinYinConsult.spellMap.put("ku", -16419);
        PinYinConsult.spellMap.put("kua", -16412);
        PinYinConsult.spellMap.put("kuai", -16407);
        PinYinConsult.spellMap.put("kuan", -16403);
        PinYinConsult.spellMap.put("kuang", -16401);
        PinYinConsult.spellMap.put("kui", -16393);
        PinYinConsult.spellMap.put("kun", -16220);
        PinYinConsult.spellMap.put("kuo", -16216);
        PinYinConsult.spellMap.put("la", -16212);
        PinYinConsult.spellMap.put("lai", -16205);
        PinYinConsult.spellMap.put("lan", -16202);
        PinYinConsult.spellMap.put("lang", -16187);
        PinYinConsult.spellMap.put("lao", -16180);
        PinYinConsult.spellMap.put("le", -16171);
        PinYinConsult.spellMap.put("lei", -16169);
        PinYinConsult.spellMap.put("leng", -16158);
        PinYinConsult.spellMap.put("li", -16155);
        PinYinConsult.spellMap.put("lia", -15959);
        PinYinConsult.spellMap.put("lian", -15958);
        PinYinConsult.spellMap.put("liang", -15944);
        PinYinConsult.spellMap.put("liao", -15933);
        PinYinConsult.spellMap.put("lie", -15920);
        PinYinConsult.spellMap.put("lin", -15915);
        PinYinConsult.spellMap.put("ling", -15903);
        PinYinConsult.spellMap.put("liu", -15889);
        PinYinConsult.spellMap.put("long", -15878);
        PinYinConsult.spellMap.put("lou", -15707);
        PinYinConsult.spellMap.put("lu", -15701);
        PinYinConsult.spellMap.put("lv", -15681);
        PinYinConsult.spellMap.put("luan", -15667);
        PinYinConsult.spellMap.put("lue", -15661);
        PinYinConsult.spellMap.put("lun", -15659);
        PinYinConsult.spellMap.put("luo", -15652);
        PinYinConsult.spellMap.put("ma", -15640);
        PinYinConsult.spellMap.put("mai", -15631);
        PinYinConsult.spellMap.put("man", -15625);
        PinYinConsult.spellMap.put("mang", -15454);
        PinYinConsult.spellMap.put("mao", -15448);
        PinYinConsult.spellMap.put("me", -15436);
        PinYinConsult.spellMap.put("mei", -15435);
        PinYinConsult.spellMap.put("men", -15419);
        PinYinConsult.spellMap.put("meng", -15416);
        PinYinConsult.spellMap.put("mi", -15408);
        PinYinConsult.spellMap.put("mian", -15394);
        PinYinConsult.spellMap.put("miao", -15385);
        PinYinConsult.spellMap.put("mie", -15377);
        PinYinConsult.spellMap.put("min", -15375);
        PinYinConsult.spellMap.put("ming", -15369);
        PinYinConsult.spellMap.put("miu", -15363);
        PinYinConsult.spellMap.put("mo", -15362);
        PinYinConsult.spellMap.put("mou", -15183);
        PinYinConsult.spellMap.put("mu", -15180);
        PinYinConsult.spellMap.put("na", -15165);
        PinYinConsult.spellMap.put("nai", -15158);
        PinYinConsult.spellMap.put("nan", -15153);
        PinYinConsult.spellMap.put("nang", -15150);
        PinYinConsult.spellMap.put("nao", -15149);
        PinYinConsult.spellMap.put("ne", -15144);
        PinYinConsult.spellMap.put("nei", -15143);
        PinYinConsult.spellMap.put("nen", -15141);
        PinYinConsult.spellMap.put("neng", -15140);
        PinYinConsult.spellMap.put("ni", -15139);
        PinYinConsult.spellMap.put("nian", -15128);
        PinYinConsult.spellMap.put("niang", -15121);
        PinYinConsult.spellMap.put("niao", -15119);
        PinYinConsult.spellMap.put("nie", -15117);
        PinYinConsult.spellMap.put("nin", -15110);
        PinYinConsult.spellMap.put("ning", -15109);
        PinYinConsult.spellMap.put("niu", -14941);
        PinYinConsult.spellMap.put("nong", -14937);
        PinYinConsult.spellMap.put("nu", -14933);
        PinYinConsult.spellMap.put("nv", -14930);
        PinYinConsult.spellMap.put("nuan", -14929);
        PinYinConsult.spellMap.put("nue", -14928);
        PinYinConsult.spellMap.put("nuo", -14926);
        PinYinConsult.spellMap.put("o", -14922);
        PinYinConsult.spellMap.put("ou", -14921);
        PinYinConsult.spellMap.put("pa", -14914);
        PinYinConsult.spellMap.put("pai", -14908);
        PinYinConsult.spellMap.put("pan", -14902);
        PinYinConsult.spellMap.put("pang", -14894);
        PinYinConsult.spellMap.put("pao", -14889);
        PinYinConsult.spellMap.put("pei", -14882);
        PinYinConsult.spellMap.put("pen", -14873);
        PinYinConsult.spellMap.put("peng", -14871);
        PinYinConsult.spellMap.put("pi", -14857);
        PinYinConsult.spellMap.put("pian", -14678);
        PinYinConsult.spellMap.put("piao", -14674);
        PinYinConsult.spellMap.put("pie", -14670);
        PinYinConsult.spellMap.put("pin", -14668);
        PinYinConsult.spellMap.put("ping", -14663);
        PinYinConsult.spellMap.put("po", -14654);
        PinYinConsult.spellMap.put("pu", -14645);
        PinYinConsult.spellMap.put("qi", -14630);
        PinYinConsult.spellMap.put("qia", -14594);
        PinYinConsult.spellMap.put("qian", -14429);
        PinYinConsult.spellMap.put("qiang", -14407);
        PinYinConsult.spellMap.put("qiao", -14399);
        PinYinConsult.spellMap.put("qie", -14384);
        PinYinConsult.spellMap.put("qin", -14379);
        PinYinConsult.spellMap.put("qing", -14368);
        PinYinConsult.spellMap.put("qiong", -14355);
        PinYinConsult.spellMap.put("qiu", -14353);
        PinYinConsult.spellMap.put("qu", -14345);
        PinYinConsult.spellMap.put("quan", -14170);
        PinYinConsult.spellMap.put("que", -14159);
        PinYinConsult.spellMap.put("qun", -14151);
        PinYinConsult.spellMap.put("ran", -14149);
        PinYinConsult.spellMap.put("rang", -14145);
        PinYinConsult.spellMap.put("rao", -14140);
        PinYinConsult.spellMap.put("re", -14137);
        PinYinConsult.spellMap.put("ren", -14135);
        PinYinConsult.spellMap.put("reng", -14125);
        PinYinConsult.spellMap.put("ri", -14123);
        PinYinConsult.spellMap.put("rong", -14122);
        PinYinConsult.spellMap.put("rou", -14112);
        PinYinConsult.spellMap.put("ru", -14109);
        PinYinConsult.spellMap.put("ruan", -14099);
        PinYinConsult.spellMap.put("rui", -14097);
        PinYinConsult.spellMap.put("run", -14094);
        PinYinConsult.spellMap.put("ruo", -14092);
        PinYinConsult.spellMap.put("sa", -14090);
        PinYinConsult.spellMap.put("sai", -14087);
        PinYinConsult.spellMap.put("san", -14083);
        PinYinConsult.spellMap.put("sang", -13917);
        PinYinConsult.spellMap.put("sao", -13914);
        PinYinConsult.spellMap.put("se", -13910);
        PinYinConsult.spellMap.put("sen", -13907);
        PinYinConsult.spellMap.put("seng", -13906);
        PinYinConsult.spellMap.put("sha", -13905);
        PinYinConsult.spellMap.put("shai", -13896);
        PinYinConsult.spellMap.put("shan", -13894);
        PinYinConsult.spellMap.put("shang", -13878);
        PinYinConsult.spellMap.put("shao", -13870);
        PinYinConsult.spellMap.put("she", -13859);
        PinYinConsult.spellMap.put("shen", -13847);
        PinYinConsult.spellMap.put("sheng", -13831);
        PinYinConsult.spellMap.put("shi", -13658);
        PinYinConsult.spellMap.put("shou", -13611);
        PinYinConsult.spellMap.put("shu", -13601);
        PinYinConsult.spellMap.put("shua", -13406);
        PinYinConsult.spellMap.put("shuai", -13404);
        PinYinConsult.spellMap.put("shuan", -13400);
        PinYinConsult.spellMap.put("shuang", -13398);
        PinYinConsult.spellMap.put("shui", -13395);
        PinYinConsult.spellMap.put("shun", -13391);
        PinYinConsult.spellMap.put("shuo", -13387);
        PinYinConsult.spellMap.put("si", -13383);
        PinYinConsult.spellMap.put("song", -13367);
        PinYinConsult.spellMap.put("sou", -13359);
        PinYinConsult.spellMap.put("su", -13356);
        PinYinConsult.spellMap.put("suan", -13343);
        PinYinConsult.spellMap.put("sui", -13340);
        PinYinConsult.spellMap.put("sun", -13329);
        PinYinConsult.spellMap.put("suo", -13326);
        PinYinConsult.spellMap.put("ta", -13318);
        PinYinConsult.spellMap.put("tai", -13147);
        PinYinConsult.spellMap.put("tan", -13138);
        PinYinConsult.spellMap.put("tang", -13120);
        PinYinConsult.spellMap.put("tao", -13107);
        PinYinConsult.spellMap.put("te", -13096);
        PinYinConsult.spellMap.put("teng", -13095);
        PinYinConsult.spellMap.put("ti", -13091);
        PinYinConsult.spellMap.put("tian", -13076);
        PinYinConsult.spellMap.put("tiao", -13068);
        PinYinConsult.spellMap.put("tie", -13063);
        PinYinConsult.spellMap.put("ting", -13060);
        PinYinConsult.spellMap.put("tong", -12888);
        PinYinConsult.spellMap.put("tou", -12875);
        PinYinConsult.spellMap.put("tu", -12871);
        PinYinConsult.spellMap.put("tuan", -12860);
        PinYinConsult.spellMap.put("tui", -12858);
        PinYinConsult.spellMap.put("tun", -12852);
        PinYinConsult.spellMap.put("tuo", -12849);
        PinYinConsult.spellMap.put("wa", -12838);
        PinYinConsult.spellMap.put("wai", -12831);
        PinYinConsult.spellMap.put("wan", -12829);
        PinYinConsult.spellMap.put("wang", -12812);
        PinYinConsult.spellMap.put("wei", -12802);
        PinYinConsult.spellMap.put("wen", -12607);
        PinYinConsult.spellMap.put("weng", -12597);
        PinYinConsult.spellMap.put("wo", -12594);
        PinYinConsult.spellMap.put("wu", -12585);
        PinYinConsult.spellMap.put("xi", -12556);
        PinYinConsult.spellMap.put("xia", -12359);
        PinYinConsult.spellMap.put("xian", -12346);
        PinYinConsult.spellMap.put("xiang", -12320);
        PinYinConsult.spellMap.put("xiao", -12300);
        PinYinConsult.spellMap.put("xie", -12120);
        PinYinConsult.spellMap.put("xin", -12099);
        PinYinConsult.spellMap.put("xing", -12089);
        PinYinConsult.spellMap.put("xiong", -12074);
        PinYinConsult.spellMap.put("xiu", -12067);
        PinYinConsult.spellMap.put("xu", -12058);
        PinYinConsult.spellMap.put("xuan", -12039);
        PinYinConsult.spellMap.put("xue", -11867);
        PinYinConsult.spellMap.put("xun", -11861);
        PinYinConsult.spellMap.put("ya", -11847);
        PinYinConsult.spellMap.put("yan", -11831);
        PinYinConsult.spellMap.put("yang", -11798);
        PinYinConsult.spellMap.put("yao", -11781);
        PinYinConsult.spellMap.put("ye", -11604);
        PinYinConsult.spellMap.put("yi", -11589);
        PinYinConsult.spellMap.put("yin", -11536);
        PinYinConsult.spellMap.put("ying", -11358);
        PinYinConsult.spellMap.put("yo", -11340);
        PinYinConsult.spellMap.put("yong", -11339);
        PinYinConsult.spellMap.put("you", -11324);
        PinYinConsult.spellMap.put("yu", -11303);
        PinYinConsult.spellMap.put("yuan", -11097);
        PinYinConsult.spellMap.put("yue", -11077);
        PinYinConsult.spellMap.put("yun", -11067);
        PinYinConsult.spellMap.put("za", -11055);
        PinYinConsult.spellMap.put("zai", -11052);
        PinYinConsult.spellMap.put("zan", -11045);
        PinYinConsult.spellMap.put("zang", -11041);
        PinYinConsult.spellMap.put("zao", -11038);
        PinYinConsult.spellMap.put("ze", -11024);
        PinYinConsult.spellMap.put("zei", -11020);
        PinYinConsult.spellMap.put("zen", -11019);
        PinYinConsult.spellMap.put("zeng", -11018);
        PinYinConsult.spellMap.put("zha", -11014);
        PinYinConsult.spellMap.put("zhai", -10838);
        PinYinConsult.spellMap.put("zhan", -10832);
        PinYinConsult.spellMap.put("zhang", -10815);
        PinYinConsult.spellMap.put("zhao", -10800);
        PinYinConsult.spellMap.put("zhe", -10790);
        PinYinConsult.spellMap.put("zhen", -10780);
        PinYinConsult.spellMap.put("zheng", -10764);
        PinYinConsult.spellMap.put("zhi", -10587);
        PinYinConsult.spellMap.put("zhong", -10544);
        PinYinConsult.spellMap.put("zhou", -10533);
        PinYinConsult.spellMap.put("zhu", -10519);
        PinYinConsult.spellMap.put("zhua", -10331);
        PinYinConsult.spellMap.put("zhuai", -10329);
        PinYinConsult.spellMap.put("zhuan", -10328);
        PinYinConsult.spellMap.put("zhuang", -10322);
        PinYinConsult.spellMap.put("zhui", -10315);
        PinYinConsult.spellMap.put("zhun", -10309);
        PinYinConsult.spellMap.put("zhuo", -10307);
        PinYinConsult.spellMap.put("zi", -10296);
        PinYinConsult.spellMap.put("zong", -10281);
        PinYinConsult.spellMap.put("zou", -10274);
        PinYinConsult.spellMap.put("zu", -10270);
        PinYinConsult.spellMap.put("zuan", -10262);
        PinYinConsult.spellMap.put("zui", -10260);
        PinYinConsult.spellMap.put("zun", -10256);
        PinYinConsult.spellMap.put("zuo", -10254);
    }

    public String getFullSpell(final String cnStr) {
        if (!this.validate(cnStr)) {
            return cnStr;
        }
        char[] chars = cnStr.toCharArray();
        StringBuffer retuBuf = new StringBuffer();
        for (int i = 0, Len = chars.length; i < Len; ++i) {
            int ascii = this.getCnAscii(chars[i]);
            if (ascii == 0) {
                retuBuf.append(chars[i]);
            } else {
                String spell = this.getSpellByAscii(ascii);
                if (spell == null) {
                    retuBuf.append(chars[i]);
                } else {
                    retuBuf.append(spell);
                }
            }
        }
        return retuBuf.toString();
    }

    private String getSpellByAscii(final int ascii) {
        if (ascii > 0 && ascii < 160) {
            return String.valueOf((char) ascii);
        }
        if (ascii < -20319 || ascii > -10247) {
            return null;
        }
        Set<String> keySet = PinYinConsult.spellMap.keySet();
        Iterator<String> it = keySet.iterator();
        String spell0 = null;
        String spell2 = null;
        int asciiRang0 = -20319;
        while (it.hasNext()) {
            spell2 = it.next();
            Integer asciiValue = PinYinConsult.spellMap.get(spell2);
            if (asciiValue != null) {
                int asciiRang2 = asciiValue;
                if (ascii >= asciiRang0 && ascii < asciiRang2) {
                    return (spell0 == null) ? spell2 : spell0;
                }
                spell0 = spell2;
                asciiRang0 = asciiRang2;
            }
        }
        return null;
    }

    private int getCnAscii(final char cn) {
        byte[] bytes = String.valueOf(cn).getBytes();
        if (bytes == null || bytes.length > 2 || bytes.length <= 0) {
            return 0;
        }
        if (bytes.length == 1) {
            return bytes[0];
        }
        if (bytes.length == 2) {
            int hightByte = 256 + bytes[0];
            int lowByte = 256 + bytes[1];
            int ascii = 256 * hightByte + lowByte - 65536;
            return ascii;
        }
        return 0;
    }

    private boolean validate(final String cnStr) {
        return cnStr != null && !cnStr.trim().equals("");
    }
}
