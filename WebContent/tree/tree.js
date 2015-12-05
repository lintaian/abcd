var zNodes =[
	{id: 510000, pId: 0, name: "四川省"},
	{id: 510100, pId: 510000, name: "成都市"},
	{id: 510101, pId: 510100, name: "市直属"},
	{id: 510104, pId: 510100, name: "锦江区"},
	{id: 510105, pId: 510100, name: "青羊区"},
	{id: 510106, pId: 510100, name: "金牛区"},
	{id: 510107, pId: 510100, name: "武侯区"},
	{id: 510108, pId: 510100, name: "成华区"},
	{id: 510109, pId: 510100, name: "高新区"},
	{id: 510110, pId: 510100, name: "天府新区"},
	{id: 510112, pId: 510100, name: "龙泉驿区"},
	{id: 510113, pId: 510100, name: "青白江区"},
	{id: 510114, pId: 510100, name: "新都区"},
	{id: 510115, pId: 510100, name: "温江区"},
	{id: 510121, pId: 510100, name: "金堂县"},
	{id: 510122, pId: 510100, name: "双流县"},
	{id: 510124, pId: 510100, name: "郫县"},
	{id: 510129, pId: 510100, name: "大邑县"},
	{id: 510131, pId: 510100, name: "蒲江县"},
	{id: 510132, pId: 510100, name: "新津县"},
	{id: 510181, pId: 510100, name: "都江堰市"},
	{id: 510182, pId: 510100, name: "彭州市"},
	{id: 510183, pId: 510100, name: "邛崃市"},
	{id: 510184, pId: 510100, name: "崇州市"},
	{id: 510300, pId: 510000, name: "自贡市"},
	{id: 510301, pId: 510300, name: "市辖区"},
	{id: 510302, pId: 510300, name: "自流井区"},
	{id: 510303, pId: 510300, name: "贡井区"},
	{id: 510304, pId: 510300, name: "大安区"},
	{id: 510311, pId: 510300, name: "沿滩区"},
	{id: 510321, pId: 510300, name: "荣县"},
	{id: 510322, pId: 510300, name: "富顺县"},
	{id: 510400, pId: 510000, name: "攀枝花市"},
	{id: 510401, pId: 510400, name: "市辖区"},
	{id: 510402, pId: 510400, name: "东区"},
	{id: 510403, pId: 510400, name: "西区"},
	{id: 510411, pId: 510400, name: "仁和区"},
	{id: 510421, pId: 510400, name: "米易县"},
	{id: 510422, pId: 510400, name: "盐边县"},
	{id: 510500, pId: 510000, name: "泸州市"},
	{id: 510501, pId: 510500, name: "市辖区"},
	{id: 510502, pId: 510500, name: "江阳区"},
	{id: 510503, pId: 510500, name: "纳溪区"},
	{id: 510504, pId: 510500, name: "龙马潭区"},
	{id: 510521, pId: 510500, name: "泸县"},
	{id: 510522, pId: 510500, name: "合江县"},
	{id: 510524, pId: 510500, name: "叙永县"},
	{id: 510525, pId: 510500, name: "古蔺县"},
	{id: 510600, pId: 510000, name: "德阳市"},
	{id: 510601, pId: 510600, name: "市辖区"},
	{id: 510603, pId: 510600, name: "旌阳区"},
	{id: 510623, pId: 510600, name: "中江县"},
	{id: 510626, pId: 510600, name: "罗江县"},
	{id: 510681, pId: 510600, name: "广汉市"},
	{id: 510682, pId: 510600, name: "什邡市"},
	{id: 510683, pId: 510600, name: "绵竹市"},
	{id: 510700, pId: 510000, name: "绵阳市"},
	{id: 510701, pId: 510700, name: "市辖区"},
	{id: 510703, pId: 510700, name: "涪城区"},
	{id: 510704, pId: 510700, name: "游仙区"},
	{id: 510722, pId: 510700, name: "三台县"},
	{id: 510723, pId: 510700, name: "盐亭县"},
	{id: 510724, pId: 510700, name: "安县"},
	{id: 510725, pId: 510700, name: "梓潼县"},
	{id: 510726, pId: 510700, name: "北川羌族自治县"},
	{id: 510727, pId: 510700, name: "平武县"},
	{id: 510781, pId: 510700, name: "江油市"},
	{id: 510800, pId: 510000, name: "广元市"},
	{id: 510801, pId: 510800, name: "市辖区"},
	{id: 510802, pId: 510800, name: "利州区"},
	{id: 510811, pId: 510800, name: "元坝区"},
	{id: 510812, pId: 510800, name: "朝天区"},
	{id: 510821, pId: 510800, name: "旺苍县"},
	{id: 510822, pId: 510800, name: "青川县"},
	{id: 510823, pId: 510800, name: "剑阁县"},
	{id: 510824, pId: 510800, name: "苍溪县"},
	{id: 510900, pId: 510000, name: "遂宁市"},
	{id: 510901, pId: 510900, name: "市辖区"},
	{id: 510903, pId: 510900, name: "船山区"},
	{id: 510904, pId: 510900, name: "安居区"},
	{id: 510921, pId: 510900, name: "蓬溪县"},
	{id: 510922, pId: 510900, name: "射洪县"},
	{id: 510923, pId: 510900, name: "大英县"},
	{id: 511000, pId: 510000, name: "内江市"},
	{id: 511001, pId: 511000, name: "市辖区"},
	{id: 511002, pId: 511000, name: "市中区"},
	{id: 511011, pId: 511000, name: "东兴区"},
	{id: 511024, pId: 511000, name: "威远县"},
	{id: 511025, pId: 511000, name: "资中县"},
	{id: 511028, pId: 511000, name: "隆昌县"},
	{id: 511100, pId: 510000, name: "乐山市"},
	{id: 511101, pId: 511100, name: "市辖区"},
	{id: 511102, pId: 511100, name: "市中区"},
	{id: 511111, pId: 511100, name: "沙湾区"},
	{id: 511112, pId: 511100, name: "五通桥区"},
	{id: 511113, pId: 511100, name: "金口河区"},
	{id: 511123, pId: 511100, name: "犍为县"},
	{id: 511124, pId: 511100, name: "井研县"},
	{id: 511126, pId: 511100, name: "夹江县"},
	{id: 511129, pId: 511100, name: "沐川县"},
	{id: 511132, pId: 511100, name: "峨边彝族自治县"},
	{id: 511133, pId: 511100, name: "马边彝族自治县"},
	{id: 511181, pId: 511100, name: "峨眉山市"},
	{id: 511300, pId: 510000, name: "南充市"},
	{id: 511301, pId: 511300, name: "市辖区"},
	{id: 511302, pId: 511300, name: "顺庆区"},
	{id: 511303, pId: 511300, name: "高坪区"},
	{id: 511304, pId: 511300, name: "嘉陵区"},
	{id: 511321, pId: 511300, name: "南部县"},
	{id: 511322, pId: 511300, name: "营山县"},
	{id: 511323, pId: 511300, name: "蓬安县"},
	{id: 511324, pId: 511300, name: "仪陇县"},
	{id: 511325, pId: 511300, name: "西充县"},
	{id: 511381, pId: 511300, name: "阆中市"},
	{id: 511400, pId: 510000, name: "眉山市"},
	{id: 511401, pId: 511400, name: "市辖区"},
	{id: 511402, pId: 511400, name: "东坡区"},
	{id: 511421, pId: 511400, name: "仁寿县"},
	{id: 511422, pId: 511400, name: "彭山县"},
	{id: 511423, pId: 511400, name: "洪雅县"},
	{id: 511424, pId: 511400, name: "丹棱县"},
	{id: 511425, pId: 511400, name: "青神县"},
	{id: 511500, pId: 510000, name: "宜宾市"},
	{id: 511501, pId: 511500, name: "市辖区"},
	{id: 511502, pId: 511500, name: "翠屏区"},
	{id: 511503, pId: 511500, name: "南溪区"},
	{id: 511521, pId: 511500, name: "宜宾县"},
	{id: 511523, pId: 511500, name: "江安县"},
	{id: 511524, pId: 511500, name: "长宁县"},
	{id: 511525, pId: 511500, name: "高县"},
	{id: 511526, pId: 511500, name: "珙县"},
	{id: 511527, pId: 511500, name: "筠连县"},
	{id: 511528, pId: 511500, name: "兴文县"},
	{id: 511529, pId: 511500, name: "屏山县"},
	{id: 511600, pId: 510000, name: "广安市"},
	{id: 511601, pId: 511600, name: "市辖区"},
	{id: 511602, pId: 511600, name: "广安区"},
	{id: 511603, pId: 511600, name: "前锋区"},
	{id: 511621, pId: 511600, name: "岳池县"},
	{id: 511622, pId: 511600, name: "武胜县"},
	{id: 511623, pId: 511600, name: "邻水县"},
	{id: 511681, pId: 511600, name: "华蓥市"},
	{id: 511700, pId: 510000, name: "达州市"},
	{id: 511701, pId: 511700, name: "市辖区"},
	{id: 511702, pId: 511700, name: "通川区"},
	{id: 511703, pId: 511700, name: "达川区"},
	{id: 511722, pId: 511700, name: "宣汉县"},
	{id: 511723, pId: 511700, name: "开江县"},
	{id: 511724, pId: 511700, name: "大竹县"},
	{id: 511725, pId: 511700, name: "渠县"},
	{id: 511781, pId: 511700, name: "万源市"},
	{id: 511800, pId: 510000, name: "雅安市"},
	{id: 511801, pId: 511800, name: "市辖区"},
	{id: 511802, pId: 511800, name: "雨城区"},
	{id: 511803, pId: 511800, name: "名山区"},
	{id: 511822, pId: 511800, name: "荥经县"},
	{id: 511823, pId: 511800, name: "汉源县"},
	{id: 511824, pId: 511800, name: "石棉县"},
	{id: 511825, pId: 511800, name: "天全县"},
	{id: 511826, pId: 511800, name: "芦山县"},
	{id: 511827, pId: 511800, name: "宝兴县"},
	{id: 511900, pId: 510000, name: "巴中市"},
	{id: 511901, pId: 511900, name: "市辖区"},
	{id: 511902, pId: 511900, name: "巴州区"},
	{id: 511903, pId: 511900, name: "恩阳区"},
	{id: 511921, pId: 511900, name: "通江县"},
	{id: 511922, pId: 511900, name: "南江县"},
	{id: 511923, pId: 511900, name: "平昌县"},
	{id: 512000, pId: 510000, name: "资阳市"},
	{id: 512001, pId: 512000, name: "市辖区"},
	{id: 512002, pId: 512000, name: "雁江区"},
	{id: 512021, pId: 512000, name: "安岳县"},
	{id: 512022, pId: 512000, name: "乐至县"},
	{id: 512081, pId: 512000, name: "简阳市"},
	{id: 513200, pId: 510000, name: "阿坝藏族羌族自治州"},
	{id: 513221, pId: 513200, name: "汶川县"},
	{id: 513222, pId: 513200, name: "理县"},
	{id: 513223, pId: 513200, name: "茂县"},
	{id: 513224, pId: 513200, name: "松潘县"},
	{id: 513225, pId: 513200, name: "九寨沟县"},
	{id: 513226, pId: 513200, name: "金川县"},
	{id: 513227, pId: 513200, name: "小金县"},
	{id: 513228, pId: 513200, name: "黑水县"},
	{id: 513229, pId: 513200, name: "马尔康县"},
	{id: 513230, pId: 513200, name: "壤塘县"},
	{id: 513231, pId: 513200, name: "阿坝县"},
	{id: 513232, pId: 513200, name: "若尔盖县"},
	{id: 513233, pId: 513200, name: "红原县"},
	{id: 513300, pId: 510000, name: "甘孜藏族自治州"},
	{id: 513321, pId: 513300, name: "康定县"},
	{id: 513322, pId: 513300, name: "泸定县"},
	{id: 513323, pId: 513300, name: "丹巴县"},
	{id: 513324, pId: 513300, name: "九龙县"},
	{id: 513325, pId: 513300, name: "雅江县"},
	{id: 513326, pId: 513300, name: "道孚县"},
	{id: 513327, pId: 513300, name: "炉霍县"},
	{id: 513328, pId: 513300, name: "甘孜县"},
	{id: 513329, pId: 513300, name: "新龙县"},
	{id: 513330, pId: 513300, name: "德格县"},
	{id: 513331, pId: 513300, name: "白玉县"},
	{id: 513332, pId: 513300, name: "石渠县"},
	{id: 513333, pId: 513300, name: "色达县"},
	{id: 513334, pId: 513300, name: "理塘县"},
	{id: 513335, pId: 513300, name: "巴塘县"},
	{id: 513336, pId: 513300, name: "乡城县"},
	{id: 513337, pId: 513300, name: "稻城县"},
	{id: 513338, pId: 513300, name: "得荣县"},
	{id: 513400, pId: 510000, name: "凉山彝族自治州"},
	{id: 513401, pId: 513400, name: "西昌市"},
	{id: 513422, pId: 513400, name: "木里藏族自治县"},
	{id: 513423, pId: 513400, name: "盐源县"},
	{id: 513424, pId: 513400, name: "德昌县"},
	{id: 513425, pId: 513400, name: "会理县"},
	{id: 513426, pId: 513400, name: "会东县"},
	{id: 513427, pId: 513400, name: "宁南县"},
	{id: 513428, pId: 513400, name: "普格县"},
	{id: 513429, pId: 513400, name: "布拖县"},
	{id: 513430, pId: 513400, name: "金阳县"},
	{id: 513431, pId: 513400, name: "昭觉县"},
	{id: 513432, pId: 513400, name: "喜德县"},
	{id: 513433, pId: 513400, name: "冕宁县"},
	{id: 513434, pId: 513400, name: "越西县"},
	{id: 513435, pId: 513400, name: "甘洛县"},
	{id: 513436, pId: 513400, name: "美姑县"},
	{id: 513437, pId: 513400, name: "雷波县"},
	 {id: 51010101, pId: 510101, name: "石室中学"},
	 {id: 51010103, pId: 510101, name: "石室北湖"},
	 {id: 51010111, pId: 510101, name: "成都七中"},
	 {id: 51010112, pId: 510101, name: "七中国际部"},
	 {id: 51010113, pId: 510101, name: "七中网络班"},
	 {id: 51010114, pId: 510101, name: "七中高新"},
	 {id: 51010121, pId: 510101, name: "树德宁夏"},
	 {id: 51010124, pId: 510101, name: "树德光华"},
	 {id: 51010125, pId: 510101, name: "树德外语"},
	 {id: 51010131, pId: 510101, name: "成都外语学校"},
	 {id: 51010132, pId: 510101, name: "四川国际学校"},
	 {id: 51010133, pId: 510101, name: "实验外语学校"},
	 {id: 51010134, pId: 510101, name: "实外朝日"},
	 {id: 51010135, pId: 510101, name: "北师大成都实验"},
	 {id: 51010136, pId: 510101, name: "盐道街外语"},
	 {id: 51010401, pId: 510104, name: "川师大附中"},
	 {id: 51010402, pId: 510104, name: "盐道街中学"},
	 {id: 51010403, pId: 510104, name: "成都17中"},
	 {id: 51010404, pId: 510104, name: "田家柄中学"},
	 {id: 51010405, pId: 510104, name: "成都3中"},
	 {id: 51010408, pId: 510104, name: "七中嘉祥外语"},
	 {id: 51010449, pId: 510104, name: "师大附中国际部"},
	 {id: 51010503, pId: 510105, name: "成都11中"},
	 {id: 51010506, pId: 510105, name: "树德协进"},
	 {id: 51010507, pId: 510105, name: "成都37中"},
	 {id: 51010508, pId: 510105, name: "成飞中学"},
	 {id: 51010517, pId: 510105, name: "川师大实验外语"},
	 {id: 51010601, pId: 510106, name: "成都18中"},
	 {id: 51010602, pId: 510106, name: "成都20中"},
	 {id: 51010605, pId: 510106, name: "成都36中"},
	 {id: 51010606, pId: 510106, name: "成都铁中"},
	 {id: 51010607, pId: 510106, name: "通锦中学"},
	 {id: 51010608, pId: 510106, name: "成都8中"},
	 {id: 51010610, pId: 510106, name: "交大附中"},
	 {id: 51010614, pId: 510106, name: "石室外语学校"},
	 {id: 51010619, pId: 510106, name: "七中万达"},
	 {id: 51010702, pId: 510107, name: "成都12中"},
	 {id: 51010703, pId: 510107, name: "西北中学"},
	 {id: 51010705, pId: 510107, name: "武侯高中"},
	 {id: 51010706, pId: 510107, name: "成都12中科华"},
	 {id: 51010707, pId: 510107, name: "石室佳兴"},
	 {id: 51010801, pId: 510108, name: "列五中学"},
	 {id: 51010802, pId: 510108, name: "华西中学"},
	 {id: 51010803, pId: 510108, name: "成大附中"},
	 {id: 51010804, pId: 510108, name: "成都38中"},
	 {id: 51010805, pId: 510108, name: "成都49中"},
	 {id: 51010806, pId: 510108, name: "成都实验中学"},
	 {id: 51010901, pId: 510109, name: "玉林中学"},
	 {id: 51010902, pId: 510109, name: "高新实验中学"},
	 {id: 51010903, pId: 510109, name: "玉林石羊"},
	 {id: 51010904, pId: 510109, name: "美视国际学校"},
	 {id: 51010905, pId: 510109, name: "电子科大实验"},
	 {id: 51010908, pId: 510109, name: "中和中学"},
	 {id: 51010910, pId: 510109, name: "石室天府"},
	 {id: 51010912, pId: 510109, name: "西藏中学"},
	 {id: 51011004, pId: 510110, name: "华阳中学"},
	 {id: 51011005, pId: 510110, name: "籍田中学"},
	 {id: 51011007, pId: 510110, name: "太平中学"},
	 {id: 51011013, pId: 510110, name: "实验外国语西区"},
	 {id: 51011201, pId: 510112, name: "龙泉中学"},
	 {id: 51011202, pId: 510112, name: "洛带中学"},
	 {id: 51011204, pId: 510112, name: "龙泉2中"},
	 {id: 51011205, pId: 510112, name: "航天中学"},
	 {id: 51011206, pId: 510112, name: "华川中学"},
	 {id: 51011289, pId: 510112, name: "经开区实验"},
	 {id: 51011301, pId: 510113, name: "大弯中学"},
	 {id: 51011302, pId: 510113, name: "川化中学"},
	 {id: 51011304, pId: 510113, name: "青白江中学"},
	 {id: 51011305, pId: 510113, name: "城厢中学"},
	 {id: 51011306, pId: 510113, name: "北大附成都实验"},
	 {id: 51011401, pId: 510114, name: "新都一中"},
	 {id: 51011402, pId: 510114, name: "新都二中"},
	 {id: 51011403, pId: 510114, name: "升庵中学"},
	 {id: 51011405, pId: 510114, name: "泰兴中学"},
	 {id: 51011408, pId: 510114, name: "香城中学"},
	 {id: 51011445, pId: 510114, name: "三原外语"},
	 {id: 51011501, pId: 510115, name: "温江中学"},
	 {id: 51011502, pId: 510115, name: "温江二中"},
	 {id: 51011505, pId: 510115, name: "新世纪外语"},
	 {id: 51011506, pId: 510115, name: "七中实验学校"},
	 {id: 51012201, pId: 510122, name: "双流中学"},
	 {id: 51012202, pId: 510122, name: "棠湖中学"},
	 {id: 51012206, pId: 510122, name: "艺体中学"},
	 {id: 51012208, pId: 510122, name: "永安中学"},
	 {id: 51012220, pId: 510122, name: "棠湖外实校"},
	 {id: 51012401, pId: 510124, name: "郫县一中"},
	 {id: 51012402, pId: 510124, name: "郫县二中"},
	 {id: 51012403, pId: 510124, name: "郫县三中"},
	 {id: 51012404, pId: 510124, name: "郫县四中"},
	 {id: 51012405, pId: 510124, name: "石室蜀都中学"},
	 {id: 51012411, pId: 510124, name: "嘉祥郫县分校"},
	 {id: 51012415, pId: 510124, name: "树德联合"},
	 {id: 51012101, pId: 510121, name: "金堂中学"},
	 {id: 51012102, pId: 510121, name: "淮口中学"},
	 {id: 51012103, pId: 510121, name: "金堂实验中学"},
	 {id: 51012104, pId: 510121, name: "高板中学"},
	 {id: 51012105, pId: 510121, name: "竹篙中学"},
	 {id: 51012901, pId: 510129, name: "大邑中学"},
	 {id: 51012902, pId: 510129, name: "安仁中学"},
	 {id: 51012903, pId: 510129, name: "大邑实验中学"},
	 {id: 51013101, pId: 510131, name: "蒲江中学"},
	 {id: 51013102, pId: 510131, name: "寿安中学"},
	 {id: 51013201, pId: 510132, name: "新津中学"},
	 {id: 51013202, pId: 510132, name: "华润高中"},
	 {id: 51013229, pId: 510132, name: "北大附新津实验"},
	 {id: 51018101, pId: 510181, name: "都江堰中学"},
	 {id: 51018102, pId: 510181, name: "青城山高中"},
	 {id: 51018104, pId: 510181, name: "八一聚源高中"},
	 {id: 51018109, pId: 510181, name: "光亚学校"},
	 {id: 51018110, pId: 510181, name: "都江堰外实校"},
	 {id: 51018111, pId: 510181, name: "玉垒中学"},
	 {id: 51018112, pId: 510181, name: "育才学校"},
	 {id: 51018201, pId: 510182, name: "彭州中学"},
	 {id: 51018202, pId: 510182, name: "彭州一中"},
	 {id: 51018203, pId: 510182, name: "石室白马"},
	 {id: 51018204, pId: 510182, name: "彭州濛阳中学"},
	 {id: 51018205, pId: 510182, name: "敖平中学"},
	 {id: 51018301, pId: 510183, name: "邛崃一中"},
	 {id: 51018302, pId: 510183, name: "邛崃二中"},
	 {id: 51018303, pId: 510183, name: "高埂中学"},
	 {id: 51018304, pId: 510183, name: "平乐中学"},
	 {id: 51018306, pId: 510183, name: "强项中学"},
	 {id: 51018401, pId: 510184, name: "崇庆中学"},
	 {id: 51018402, pId: 510184, name: "树德怀远"},
	 {id: 51018403, pId: 510184, name: "蜀城中学"}
	];
var setting = {
	edit: {
		enable: true,
		showRemoveBtn: false,
		showRenameBtn: false,
		drag: {
			isCopy: true,
			isMove: false
		}
	},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeDrag: beforeDrag,
		beforeDrop: beforeDrop
	}
};
var setting2 = {
	edit: {
		enable: true,
		showRemoveBtn: true,
		showRenameBtn: true,
		editNameSelectAll: true
	},
	data: {
		simpleData: {
			enable: true,
			rootPId: 0
		}
	},
	callback: {
		beforeDrag: beforeDrag,
		beforeDrop: beforeDrop
	},
	view: {
		addHoverDom: addHoverDom,
		removeHoverDom: removeHoverDom,
	}
};
function beforeDrag(treeId, treeNodes) {
	for (var i=0,l=treeNodes.length; i<l; i++) {
		if (treeNodes[i].drag === false) {
			return false;
		}
	}
	return true;
}
function beforeDrop(treeId, treeNodes, targetNode, moveType) {
	return targetNode ? targetNode.drop !== false : true;
}
function addHoverDom(treeId, treeNode) {
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
	var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
		+ "' title='add node' onfocus='this.blur();'></span>";
	sObj.after(addStr);
	var btn = $("#addBtn_"+treeNode.tId);
	if (btn) btn.bind("click", function(){
		var zTree = $.fn.zTree.getZTreeObj("demo2");
		var id = getId(treeNode);
		zTree.addNodes(treeNode, {id: id, pId:treeNode.id, name: "new node_" + id});
		return false;
	});
};
function removeHoverDom(treeId, treeNode) {
	$("#addBtn_"+treeNode.tId).unbind().remove();
};

function getId(node) {
	var id = node.id * 100;
	for (var i = 0; i < node.children.length; i++) {
		if (node.children[i].id > id) {
			id = node.children[i].id;
		}
	}
	return id+1;
}
function parseData(nodes, rs) {
	for (var i = 0; i < nodes.length; i++) {
		rs.push({
			id: nodes[i].id,
			pId: nodes[i].pId,
			name: nodes[i].name
		});
		if (nodes[i].children) {
			parseData(nodes[i].children, rs);
		}
	}
}

$(function(){
	$.fn.zTree.init($("#demo"), setting, zNodes);
	$.fn.zTree.init($("#demo2"), setting2);
	$('body').on('click', '.get-result', function() {
		var nodes = $.fn.zTree.getZTreeObj("demo2").getNodes();
		var rs = [];
		parseData(nodes, rs);
		$('.result').html(JSON.stringify(rs));
	});
});