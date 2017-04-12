package tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import csb.Team;
import csb.entities.CheckPoint;
import csb.entities.Pod;
import csb.game.PhysicsEngine;
import trigonometry.Point;

public class PhysicsTest {

  private static final int BOOST = 650;
  private static final int SHIELD = -1;

  @Test
  public void firstBatch() throws Exception {
    CheckPoint fakeCP = new CheckPoint(0, 0, 0);
    Team team1 = new Team();
    Team team2 = new Team();
    
    Pod pod1 = new Pod(0, team1);
    Pod pod2 = new Pod(1, team1);
    Pod pod3 = new Pod(2, team2);
    Pod pod4 = new Pod(3, team2);
    
    
    PhysicsEngine physics = new PhysicsEngine();
    physics.pods = new Pod[] { pod1, pod2, pod3, pod4 };
    physics.checkPoints = new CheckPoint[] { fakeCP, fakeCP };

    pod1.readInput(2616, 6503, 0, 0, -82, 1);
    pod2.readInput(2754, 7493, 0, 0, 97, 1);
    pod3.readInput(2478, 5512, 0, 0, -82, 1);
    pod4.readInput(2892, 8484, 0, 0, 97, 1);

    applyPodFirstTurn(pod1, 3776, 3736, 94);
    applyPodFirstTurn(pod2, 3104, 10472, 85);
    applyPodFirstTurn(pod3, 10021, 5974, 100);
    applyPodFirstTurn(pod4, 10021, 5974, 100);
    physics.simulate();
    checkPod(pod1, 2652, 6416, 30, -73, 293, 1);
    checkPod(pod2, 2764, 7577, 8, 71, 83, 1);
    checkPod(pod3, 2578, 5518, 84, 5, 4, 1);
    checkPod(pod4, 2986, 8451, 80, -28, 341, 1);
    
    applyPod(pod1, 4602, 4136, 77);
    applyPod(pod2, 3911, 10349, 86);
    applyPod(pod3, 9685, 5954, BOOST);
    applyPod(pod4, 9701, 6086, 100);
    physics.simulate();
    checkPod(pod1, 2732, 6284, 68, -111, 311, 1);
    checkPod(pod2, 2778, 7664, -27, -22, 68, 1);
    checkPod(pod3, 3311, 5563, 622, 38, 4, 1);
    checkPod(pod4, 3187, 8454, 210, 98, 341, 1);

    applyPod(pod1, 4823, 4133, 90);
    applyPod(pod2, 4648, 10009, 83);
    applyPod(pod3, 7533, 5822, 100);
    applyPod(pod4, 9181, 5582, 100);
    physics.simulate();
    checkPod(pod1, 2863, 6108, 111, -149, 314, 1);
    checkPod(pod2, 2803, 7707, 21, 36, 51, 1);
    checkPod(pod3, 4033, 5607, 613, 37, 4, 1);
    checkPod(pod4, 3487, 8509, 255, 46, 334, 1);

    applyPod(pod1, 5363, 4450, 94);
    applyPod(pod2, 4241, 10339, 80);
    applyPod(pod3, 7569, 5826, 100);
    applyPod(pod4, 9001, 5790, 100);
    physics.simulate();
    checkPod(pod1, 3052, 5907, 160, -170, 326, 1);
    checkPod(pod2, 2862, 7813, 50, 90, 61, 1);
    checkPod(pod3, 4746, 5650, 605, 36, 4, 1);
    checkPod(pod4, 3832, 8511, 292, 1, 334, 1);

    applyPod(pod1, 5737, 4570, 98);
    applyPod(pod2, 4981, 9935, 86);
    applyPod(pod3, 7601, 5830, 100);
    applyPod(pod4, 8853, 5970, 100);
    physics.simulate();
    checkPod(pod1, 3300, 5693, 210, -181, 334, 1);
    checkPod(pod2, 2973, 7964, 94, 128, 45, 1);
    checkPod(pod3, 5451, 5692, 599, 35, 4, 1);
    checkPod(pod4, 4213, 8467, 324, -37, 333, 1);

    applyPod(pod1, 6262, 5217, 86);
    applyPod(pod2, 4880, 10279, 93);
    applyPod(pod3, 7625, 5834, 100);
    applyPod(pod4, 8725, 6122, 100);
    physics.simulate();
    checkPod(pod1, 3595, 5498, 250, -165, 351, 1);
    checkPod(pod2, 3126, 8164, 130, 169, 51, 1);
    checkPod(pod3, 6150, 5734, 593, 35, 4, 1);
    checkPod(pod4, 4626, 8384, 350, -70, 333, 1);

    applyPod(pod1, 6555, 5010, 100);
    applyPod(pod2, 5267, 10264, 88);
    applyPod(pod3, 7649, 5834, 100);
    applyPod(pod4, 8621, 6254, 100);
    physics.simulate();
    checkPod(pod1, 3944, 5317, 296, -154, 351, 1);
    checkPod(pod2, 3319, 8395, 163, 196, 44, 1);
    checkPod(pod3, 6843, 5776, 588, 35, 4, 1);
    checkPod(pod4, 5064, 8267, 372, -99, 332, 1);

    applyPod(pod1, 6921, 5680, 88);
    applyPod(pod2, 5852, 10001, 82);
    applyPod(pod3, 11590, 1785, 100);
    applyPod(pod4, 8533, 6370, 100);
    physics.simulate();
    checkPod(pod1, 4327, 5174, 325, -121, 7, 1);
    checkPod(pod2, 3551, 8635, 197, 203, 32, 1);
    checkPod(pod3, 7528, 5786, 582, 8, 346, 1);
    checkPod(pod4, 5524, 8120, 390, -124, 331, 1);

    applyPod(pod1, 7301, 5563, 81);
    applyPod(pod2, 6422, 9502, 91);
    applyPod(pod3, 11614, 1893, 100);
    applyPod(pod4, 8461, 6470, 100);
    physics.simulate();
    checkPod(pod1, 4732, 5064, 344, -93, 7, 1);
    checkPod(pod2, 3835, 8864, 241, 194, 17, 1);
    checkPod(pod3, 8195, 5741, 566, -38, 328, 1);
    checkPod(pod4, 6001, 7947, 405, -147, 331, 1);

    applyPod(pod1, 7514, 6185, 94);
    applyPod(pod2, 6826, 9088, 96);
    applyPod(pod3, 11678, 2077, 100);
    applyPod(pod4, 8401, 6562, 100);
    physics.simulate();
    checkPod(pod1, 5163, 5006, 366, -49, 22, 1);
    checkPod(pod2, 4172, 9065, 286, 170, 4, 1);
    checkPod(pod3, 8830, 5631, 539, -93, 314, 1);
    checkPod(pod4, 6493, 7750, 417, -167, 330, 1);

    applyPod(pod1, 7720, 6574, 78);
    applyPod(pod2, 7154, 9386, 91);
    applyPod(pod3, 11786, 2297, 0);
    applyPod(pod4, 8353, 6642, 100);
    physics.simulate();
    checkPod(pod1, 5595, 4998, 367, -6, 32, 1);
    checkPod(pod2, 4548, 9245, 320, 152, 6, 1);
    checkPod(pod3, 9369, 5538, 458, -79, 312, 1);
    checkPod(pod4, 6996, 7532, 427, -185, 329, 1);

    applyPod(pod1, 8256, 6381, 91);
    applyPod(pod2, 7294, 10452, 98);
    applyPod(pod3, 12110, 2241, 0);
    applyPod(pod4, 8313, 6714, 100);
    physics.simulate();
    checkPod(pod1, 6043, 5034, 380, 30, 27, 1);
    checkPod(pod2, 4958, 9436, 348, 162, 24, 1);
    checkPod(pod3, 9827, 5459, 389, -67, 310, 2);
    checkPod(pod4, 7508, 7294, 435, -202, 328, 1);

    applyPod(pod1, 8826, 6153, 79);
    applyPod(pod2, 7930, 9844, 89);
    applyPod(pod3, 12386, 2193, 100);
    applyPod(pod4, 8281, 6782, 100);
    physics.simulate();
    checkPod(pod1, 6496, 5093, 385, 50, 22, 1);
    checkPod(pod2, 5394, 9610, 370, 147, 8, 1);
    checkPod(pod3, 10278, 5313, 383, -123, 308, 2);
    checkPod(pod4, 8026, 7037, 440, -218, 326, 1);

    applyPod(pod1, 8794, 7020, 82);
    applyPod(pod2, 8391, 9742, 94);
    applyPod(pod3, 12410, 2417, 100);
    applyPod(pod4, 12182, 2797, 100);
    physics.simulate();
    checkPod(pod1, 6944, 5196, 380, 87, 40, 1);
    checkPod(pod2, 5858, 9761, 394, 128, 3, 1);
    checkPod(pod3, 10720, 5109, 375, -173, 306, 2);
    checkPod(pod4, 8536, 6748, 433, -246, 314, 1);

    applyPod(pod1, 9609, 6571, 85);
    applyPod(pod2, 8794, 9149, 76);
    applyPod(pod3, 12442, 2617, 100);
    applyPod(pod4, 12210, 2909, 100);
    physics.simulate();
    checkPod(pod1, 7400, 5322, 387, 107, 27, 1);
    checkPod(pod2, 6326, 9873, 398, 95, 348, 1);
    checkPod(pod3, 11152, 4854, 367, -216, 305, 2);
    checkPod(pod4, 9038, 6430, 426, -270, 314, 1);

    applyPod(pod1, 10231, 6314, 98);
    applyPod(pod2, 9075, 8673, 81);
    applyPod(pod3, 12474, 2789, 100);
    applyPod(pod4, 12238, 3005, 100);
    physics.simulate();
    checkPod(pod1, 7879, 5461, 407, 118, 19, 1);
    checkPod(pod2, 6798, 9936, 401, 53, 336, 1);
    checkPod(pod3, 11573, 4554, 357, -255, 303, 2);
    checkPod(pod4, 9532, 6087, 420, -291, 313, 2);

    applyPod(pod1, 10865, 5746, 90);
    applyPod(pod2, 9773, 9553, 98);
    applyPod(pod3, 12514, 2945, 100);
    applyPod(pod4, 12262, 3089, 100);
    physics.simulate();
    checkPod(pod1, 8376, 5588, 422, 107, 5, 1);
    checkPod(pod2, 7296, 9976, 423, 34, 353, 1);
    checkPod(pod3, 11980, 4213, 346, -290, 300, 2);
    checkPod(pod4, 10019, 5722, 414, -310, 312, 2);

    applyPod(pod1, 11357, 5254, 94);
    applyPod(pod2, 10149, 9048, 78);
    applyPod(pod3, 12558, 3085, 100);
    applyPod(pod4, 12286, 3165, 100);
    physics.simulate();
    checkPod(pod1, 8891, 5685, 438, 82, 354, 1);
    checkPod(pod2, 7793, 9986, 422, 8, 342, 1);
    checkPod(pod3, 12372, 3834, 332, -322, 297, 2);
    checkPod(pod4, 10499, 5337, 408, -327, 312, 2);

    applyPod(pod1, 11634, 4472, 82);
    applyPod(pod2, 10512, 8720, 88);
    applyPod(pod3, 6722, 4573, 0);
    applyPod(pod4, 12310, 3233, 100);
    physics.simulate();
    checkPod(pod1, 9404, 5734, 436, 41, 336, 1);
    checkPod(pod2, 8295, 9957, 426, -24, 335, 1);
    checkPod(pod3, 12704, 3512, 282, -273, 279, 2);
    checkPod(pod4, 10972, 4934, 402, -342, 311, 2);

    applyPod(pod1, 11870, 4026, 94);
    applyPod(pod2, 11140, 9006, 92);
    applyPod(pod3, 6922, 4377, 100);
    applyPod(pod4, 12334, 3293, 100);
    physics.simulate();
    checkPod(pod1, 9917, 5721, 436, -10, 325, 2);
    checkPod(pod2, 8808, 9904, 436, -45, 342, 1);
    checkPod(pod3, 12971, 3140, 226, -316, 261, 2);
    checkPod(pod4, 11438, 4515, 395, -356, 310, 2);

    applyPod(pod1, 11766, 3358, 97);
    applyPod(pod2, 11419, 8428, 89);
    applyPod(pod3, 7146, 4549, 100);
    applyPod(pod4, 12362, 3349, 100);
    physics.simulate();
    checkPod(pod1, 10413, 5635, 421, -73, 308, 2);
    checkPod(pod2, 9321, 9815, 436, -75, 331, 1);
    checkPod(pod3, 13152, 2735, 153, -344, 243, 2);
    checkPod(pod4, 11895, 4081, 388, -369, 308, 2);

    applyPod(pod1, 12251, 3264, 99);
    applyPod(pod2, 11769, 8081, 84);
    applyPod(pod3, 7438, 4661, 52);
    applyPod(pod4, 6498, 4761, 0);
    physics.simulate();
    checkPod(pod1, 10895, 5484, 409, -128, 308, 2);
    checkPod(pod2, 9826, 9691, 428, -105, 325, 1);
    checkPod(pod3, 13268, 2354, 98, -323, 225, 2);
    checkPod(pod4, 12283, 3712, 329, -313, 290, 2);

    applyPod(pod1, 12706, 3092, 94);
    applyPod(pod2, 11664, 7320, 88);
    applyPod(pod3, 7658, 4577, 0);
    applyPod(pod4, 6734, 4537, 100);
    physics.simulate();
    checkPod(pod1, 11361, 5281, 395, -172, 307, 2);
    checkPod(pod2, 10308, 9516, 409, -148, 308, 1);
    checkPod(pod3, 13366, 2031, 83, -274, 207, 3);
    checkPod(pod4, 12616, 3299, 283, -350, 272, 2);

    applyPod(pod1, 13177, 2893, 99);
    applyPod(pod2, 11526, 6774, 79);
    applyPod(pod3, 7718, 4381, 100);
    applyPod(pod4, 6918, 4685, 100);
    physics.simulate();
    checkPod(pod1, 11816, 5030, 386, -213, 307, 2);
    checkPod(pod2, 10749, 9296, 374, -187, 294, 1);
    checkPod(pod3, 13350, 1741, -13, -246, 189, 3);
    checkPod(pod4, 12872, 2853, 217, -379, 254, 2);

    applyPod(pod1, 12948, 2251, 99);
    applyPod(pod2, 11316, 6350, 98);
    applyPod(pod3, 8102, 4269, 100);
    applyPod(pod4, 7182, 4801, 25);
    physics.simulate();
    checkPod(pod1, 12239, 4725, 359, -258, 292, 2);
    checkPod(pod2, 11142, 9013, 333, -240, 281, 1);
    checkPod(pod3, 13238, 1510, -95, -195, 171, 3);
    checkPod(pod4, 13075, 2453, 172, -339, 236, 2);

    applyPod(pod1, 13542, 2022, 90);
    applyPod(pod2, 10811, 6031, 86);
    applyPod(pod3, 8430, 4065, 100);
    applyPod(pod4, 7362, 4641, 0);
    physics.simulate();
    checkPod(pod1, 12637, 4386, 338, -288, 296, 2);
    checkPod(pod2, 11466, 8688, 274, -276, 264, 1);
    checkPod(pod3, 13048, 1325, -181, -283, 153, 3);
    checkPod(pod4, 13253, 2149, 170, -132, 218, 2);

    applyPod(pod1, 13105, 1422, 89);
    applyPod(pod2, 10374, 5893, 83);
    applyPod(pod3, 8774, 4417, 100);
    applyPod(pod4, 7370, 3813, 85);
    physics.simulate();
    checkPod(pod1, 12989, 4010, 299, -319, 279, 2);
    checkPod(pod2, 11710, 8335, 207, -300, 249, 1);
    checkPod(pod3, 12786, 1101, -222, -190, 144, 3);
    checkPod(pod4, 13343, 1987, 76, -137, 200, 2);

    applyPod(pod1, 12555, 1041, 98);
    applyPod(pod2, 9926, 5922, 89);
    applyPod(pod3, 8938, 4045, 100);
    applyPod(pod4, 7746, 3833, 0);
    physics.simulate();
    checkPod(pod1, 13274, 3594, 242, -353, 262, 2);
    checkPod(pod2, 11864, 7963, 130, -315, 234, 1);
    checkPod(pod3, 12485, 972, -256, -109, 143, 3);
    checkPod(pod4, 13419, 1850, 64, -116, 182, 3);

    applyPod(pod1, 13348, 594, 95);
    applyPod(pod2, 9625, 5965, 85);
    applyPod(pod3, 9074, 3721, 100);
    applyPod(pod4, 7794, 3749, 100);
    physics.simulate();
    checkPod(pod1, 13518, 3146, 207, -380, 271, 2);
    checkPod(pod2, 11931, 7591, 56, -315, 222, 1);
    checkPod(pod3, 12151, 926, -283, -39, 141, 3);
    checkPod(pod4, 13387, 1761, -27, -75, 164, 3);

    applyPod(pod1, 13065, 180, 87);
    applyPod(pod2, 9191, 6368, 92);
    applyPod(pod3, 9182, 3441, 100);
    applyPod(pod4, 8158, 3585, 100);
    physics.simulate();
    checkPod(pod1, 13712, 2680, 164, -396, 261, 2);
    checkPod(pod2, 11903, 7239, -23, -299, 204, 1);
    checkPod(pod3, 11792, 952, -305, 21, 140, 3);
    checkPod(pod4, 13266, 1719, -103, -35, 161, 3);

    applyPod(pod1, 12362, 0, 96);
    applyPod(pod2, 8979, 6566, 84);
    applyPod(pod3, 9270, 3201, 100);
    applyPod(pod4, 8462, 3425, 100);
    physics.simulate();
    checkPod(pod1, 13833, 2198, 102, -409, 243, 3);
    checkPod(pod2, 11798, 6921, -89, -270, 193, 1);
    checkPod(pod3, 11412, 1040, -322, 74, 138, 3);
    checkPod(pod4, 13069, 1717, -167, -1, 160, 3);

    applyPod(pod1, 11770, 19, 96);
    applyPod(pod2, 8846, 6386, 91);
    applyPod(pod3, 9338, 2989, 100);
    applyPod(pod4, 8718, 3289, 100);
    physics.simulate();
    checkPod(pod1, 13869, 1719, 30, -406, 227, 3);
    checkPod(pod2, 11619, 6635, -151, -243, 190, 1);
    checkPod(pod3, 11017, 1182, -335, 121, 137, 3);
    checkPod(pod4, 12808, 1750, -221, 28, 160, 3);

    applyPod(pod1, 11471, -83, 78);
    applyPod(pod2, 8624, 6453, 89);
    applyPod(pod3, 9390, 2801, 100);
    applyPod(pod4, 8934, 3173, 100);
    physics.simulate();
    checkPod(pod1, 13837, 1266, -27, -384, 217, 3);
    checkPod(pod2, 11379, 6387, -203, -211, 183, 1);
    checkPod(pod3, 10611, 1374, -345, 162, 135, 3);
    checkPod(pod4, 12493, 1812, -267, 53, 160, 3);

    applyPod(pod1, 11310, -351, 100);
    applyPod(pod2, 8445, 5760, 84);
    applyPod(pod3, 9430, 2637, 100);
    applyPod(pod4, 9118, 3073, 100);
    physics.simulate();
    checkPod(pod1, 13726, 828, -94, -372, 213, 3);
    checkPod(pod2, 11094, 6158, -242, -194, 192, 1);
    checkPod(pod3, 10198, 1609, -351, 199, 133, 3);
    checkPod(pod4, 12132, 1900, -306, 74, 160, 3);

    applyPod(pod1, 10999, -424, 95);
    applyPod(pod2, 8343, 4960, 78);
    applyPod(pod3, 9454, 2489, 100);
    applyPod(pod4, 9274, 2989, 100);
    physics.simulate();
    checkPod(pod1, 13546, 416, -153, -349, 205, 3);
    checkPod(pod2, 10780, 5933, -266, -191, 204, 1);
    checkPod(pod3, 9782, 1884, -353, 234, 130, 3);
    checkPod(pod4, 11733, 2010, -339, 93, 159, 3);

    applyPod(pod1, 10852, -905, 95);
    applyPod(pod2, 8262, 4301, 91);
    applyPod(pod3, 4097, 6062, 100);
    applyPod(pod4, 9406, 2913, 100);
    physics.simulate();
    checkPod(pod1, 13308, 25, -202, -332, 206, 3);
    checkPod(pod2, 10438, 5693, -291, -204, 213, 2);
    checkPod(pod3, 9348, 2177, -368, 249, 144, 3);
    checkPod(pod4, 11301, 2139, -367, 109, 159, 3);

    applyPod(pod1, 10505, -1044, 77);
    applyPod(pod2, 7717, 4428, 79);
    applyPod(pod3, 4157, 6002, 100);
    applyPod(pod4, 9518, 2849, 100);
    physics.simulate();
    checkPod(pod1, 13034, -334, -232, -305, 201, 3);
    checkPod(pod2, 10075, 5456, -308, -201, 205, 2);
    checkPod(pod3, 8899, 2485, -381, 262, 144, 3);
    checkPod(pod4, 10841, 2285, -390, 124, 158, 3);

    applyPod(pod1, 10365, -1704, 85);
    applyPod(pod2, 7853, 3439, 86);
    applyPod(pod3, 4209, 5950, 100);
    applyPod(pod4, 9610, 2789, 100);
    physics.simulate();
    checkPod(pod1, 12726, -678, -261, -292, 207, 3);
    checkPod(pod2, 9703, 5197, -315, -219, 222, 2);
    checkPod(pod3, 8438, 2806, -392, 273, 144, 3);
    checkPod(pod4, 10358, 2447, -410, 137, 158, 3);

    applyPod(pod1, 9898, -1680, 88);
    applyPod(pod2, 7870, 2821, 88);
    applyPod(pod3, 4253, 5906, 100);
    applyPod(pod4, 4325, 6450, 100);
    physics.simulate();
    checkPod(pod1, 12382, -999, -292, -273, 200, 3);
    checkPod(pod2, 9334, 4908, -313, -245, 232, 2);
    checkPod(pod3, 7966, 3139, -401, 282, 143, 0);
    checkPod(pod4, 9865, 2639, -419, 163, 146, 3);

    applyPod(pod1, 9428, -1522, 92);
    applyPod(pod2, 8038, 2202, 87);
    applyPod(pod3, 4289, 5870, 100);
    applyPod(pod4, 4361, 6346, 100);
    physics.simulate();
    checkPod(pod1, 11999, -1288, -325, -245, 190, 3);
    checkPod(pod2, 8983, 4585, -297, -274, 244, 2);
    checkPod(pod3, 7485, 3481, -409, 290, 143, 0);
    checkPod(pod4, 9363, 2858, -426, 186, 146, 3);

    applyPod(pod1, 9004, -1102, 99);
    applyPod(pod2, 8518, 1621, 86);
    applyPod(pod3, 4321, 5838, 100);
    applyPod(pod4, 4389, 6254, 100);
    physics.simulate();
    checkPod(pod1, 11575, -1527, -360, -203, 176, 3);
    checkPod(pod2, 8673, 4226, -263, -305, 261, 2);
    checkPod(pod3, 6996, 3831, -415, 297, 143, 0);
    checkPod(pod4, 8854, 3100, -432, 206, 146, 3);

    applyPod(pod1, 8772, -457, 95);
    applyPod(pod2, 8303, 1248, 89);
    applyPod(pod3, 4345, 5810, 100);
    applyPod(pod4, 4413, 6174, SHIELD);
    physics.simulate();
    checkPod(pod1, 11126, -1696, -381, -143, 159, 3);
    checkPod(pod2, 8341, 4328, -343, 607, 263, 2);
    checkPod(pod3, 6501, 4188, -420, 303, 143, 0);
    checkPod(pod4, 8428, 3256, -356, 80, 145, 0);

    applyPod(pod1, 8704, 74, 96);
    applyPod(pod2, 8267, 1328, 98);
    applyPod(pod3, 4365, 5786, 100);
    applyPod(pod4, 4109, 6678, 100);
    physics.simulate();
    checkPod(pod1, 10667, -1782, -389, -73, 144, 3);
    checkPod(pod2, 7996, 4837, -293, 432, 269, 2);
    checkPod(pod3, 6001, 4551, -425, 308, 143, 0);
    checkPod(pod4, 8072, 3336, -302, 68, 142, 0);

    applyPod(pod1, 8803, 569, 95);
    applyPod(pod2, 8871, 1967, 90);
    applyPod(pod3, 4385, 5766, 100);
    applyPod(pod4, 3893, 6726, 100);
    physics.simulate();
    checkPod(pod1, 10219, -1781, -380, 1, 128, 3);
    checkPod(pod2, 7729, 5183, -227, 293, 287, 2);
    checkPod(pod3, 5496, 4919, -429, 312, 143, 0);
    checkPod(pod4, 7770, 3404, -256, 57, 141, 0);

    applyPod(pod1, 8964, 944, 96);
    applyPod(pod2, 8356, 2249, 100);
    applyPod(pod3, 4401, 5750, 100);
    applyPod(pod4, 3709, 6770, 100);
    physics.simulate();
    checkPod(pod1, 9799, -1693, -357, 74, 115, 3);
    checkPod(pod2, 7523, 5378, -175, 165, 282, 2);
    checkPod(pod3, 4987, 5291, -432, 316, 143, 0);
    checkPod(pod4, 7514, 3461, -217, 48, 140, 0);

    applyPod(pod1, 8427, 975, 91);
    applyPod(pod2, 8814, 2670, 87);
    applyPod(pod3, 11749, 4710, 0);
    applyPod(pod4, 3553, 6806, 100);
    physics.simulate();
    checkPod(pod1, 9400, -1538, -338, 131, 117, 3);
    checkPod(pod2, 7385, 5464, -116, 73, 295, 2);
    checkPod(pod3, 4555, 5607, -367, 268, 125, 0);
    checkPod(pod4, 7221, 3574, -249, 95, 140, 0);

    applyPod(pod1, 8311, 1257, 77);
    applyPod(pod2, 9424, 3264, 96);
    applyPod(pod3, 11489, 4902, 0);
    applyPod(pod4, 3681, 6618, 100);
    physics.simulate();
    checkPod(pod1, 9034, -1335, -311, 172, 111, 3);
    checkPod(pod2, 7334, 5467, -43, 2, 313, 2);
    checkPod(pod3, 4188, 5875, -311, 227, 107, 0);
    checkPod(pod4, 6896, 3734, -276, 136, 139, 0);

    applyPod(pod1, 8663, 1642, 76);
    applyPod(pod2, 9824, 3794, 85);
    applyPod(pod3, 11265, 5066, 100);
    applyPod(pod4, 3789, 6454, 100);
    physics.simulate();
    checkPod(pod1, 8714, -1088, -272, 210, 97, 3);
    checkPod(pod2, 7362, 5422, 23, -38, 326, 2);
    checkPod(pod3, 3879, 6202, -262, 277, 89, 0);
    checkPod(pod4, 6545, 3936, -298, 171, 139, 0);

    applyPod(pod1, 8813, 1910, 80);
    applyPod(pod2, 9424, 3243, 95);
    applyPod(pod3, 11069, 4866, 75);
    applyPod(pod4, 3877, 6314, 100);
    physics.simulate();
    checkPod(pod1, 8445, -798, -228, 246, 88, 3);
    checkPod(pod2, 7450, 5315, 75, -90, 313, 2);
    checkPod(pod3, 3642, 6550, -201, 295, 71, 0);
    checkPod(pod4, 6172, 4174, -316, 201, 138, 0);

    applyPod(pod1, 9342, 2064, 96);
    applyPod(pod2, 9951, 3659, 84);
    applyPod(pod3, 10825, 4794, 0);
    applyPod(pod4, 3949, 6194, 100);
    physics.simulate();
    checkPod(pod1, 8246, -460, -169, 286, 73, 3);
    checkPod(pod2, 7595, 5179, 123, -115, 326, 2);
    checkPod(pod3, 3441, 6845, -170, 250, 53, 0);
    checkPod(pod4, 5782, 4442, -331, 228, 138, 0);

    applyPod(pod1, 9022, 2437, 87);
    applyPod(pod2, 10451, 4261, 90);
    applyPod(pod3, 10701, 4974, 0);
    applyPod(pod4, 4009, 6086, 100);
    physics.simulate();
    checkPod(pod1, 8100, -90, -124, 314, 75, 3);
    checkPod(pod2, 7804, 5036, 177, -121, 342, 2);
    checkPod(pod3, 3271, 7095, -144, 212, 35, 1);
    checkPod(pod4, 5378, 4738, -343, 251, 137, 0);

    applyPod(pod1, 8100, 2909, 95);
    applyPod(pod2, 10362, 3468, 76);
    applyPod(pod3, 10597, 5126, 100);
    applyPod(pod4, 4057, 5994, 100);
    physics.simulate();
    checkPod(pod1, 7976, 319, -105, 347, 90, 3);
    checkPod(pod2, 8046, 4875, 205, -136, 328, 2);
    checkPod(pod3, 3223, 7336, -41, 204, 17, 1);
    checkPod(pod4, 4963, 5058, -353, 271, 136, 0);

    applyPod(pod1, 8792, 3205, 78);
    applyPod(pod2, 10789, 3661, 79);
    applyPod(pod3, 10185, 5158, 100);
    applyPod(pod4, 4097, 5914, 100);
    physics.simulate();
    checkPod(pod1, 7892, 741, -71, 358, 74, 3);
    checkPod(pod2, 8323, 4707, 235, -142, 336, 2);
    checkPod(pod3, 3282, 7538, 50, 171, 359, 1);
    checkPod(pod4, 4539, 5399, -360, 290, 135, 0);

    applyPod(pod1, 8211, 3723, 78);
    applyPod(pod2, 10686, 2858, 93);
    applyPod(pod3, 9821, 5290, 100);
    applyPod(pod4, 11461, 4814, 0);
    physics.simulate();
    checkPod(pod1, 7829, 1177, -53, 370, 84, 3);
    checkPod(pod2, 8631, 4508, 262, -169, 322, 2);
    checkPod(pod3, 3427, 7676, 122, 117, 341, 1);
    checkPod(pod4, 4179, 5689, -306, 246, 117, 0);

    applyPod(pod1, 8182, 4156, 87);
    applyPod(pod2, 11118, 2831, 83);
    applyPod(pod3, 9533, 5506, 100);
    applyPod(pod4, 11245, 4990, 100);
    physics.simulate();
    checkPod(pod1, 7786, 1633, -36, 387, 83, 3);
    checkPod(pod2, 8962, 4293, 281, -183, 326, 2);
    checkPod(pod3, 3643, 7760, 183, 70, 340, 1);
    checkPod(pod4, 3857, 6034, -273, 292, 99, 0);

    applyPod(pod1, 7629, 4628, 95);
    applyPod(pod2, 11697, 3062, 88);
    applyPod(pod3, 9289, 5694, 100);
    applyPod(pod4, 11113, 4806, 100);
    physics.simulate();
    checkPod(pod1, 7745, 2115, -34, 409, 93, 3);
    checkPod(pod2, 9323, 4074, 307, -186, 336, 2);
    checkPod(pod3, 3920, 7796, 235, 30, 340, 1);
    checkPod(pod4, 3599, 6425, -219, 332, 81, 0);

    applyPod(pod1, 7090, 5042, 97);
    applyPod(pod2, 12047, 2819, 82);
    applyPod(pod3, 9081, 5854, 100);
    applyPod(pod4, 10897, 4646, 33);
    physics.simulate();
    checkPod(pod1, 7690, 2619, -46, 428, 103, 3);
    checkPod(pod2, 9704, 3854, 324, -187, 335, 2);
    checkPod(pod3, 4249, 7791, 279, -4, 339, 1);
    checkPod(pod4, 3395, 6786, -173, 307, 63, 0);

    applyPod(pod1, 6705, 5452, 96);
    applyPod(pod2, 12677, 3458, 86);
    applyPod(pod3, 8905, 5990, 100);
    applyPod(pod4, 10713, 4746, 0);
    physics.simulate();
    checkPod(pod1, 7612, 3138, -65, 440, 109, 0);
    checkPod(pod2, 10113, 3656, 347, -168, 352, 2);
    checkPod(pod3, 4621, 7751, 316, -34, 339, 1);
    checkPod(pod4, 3222, 7093, -147, 260, 45, 1);

    applyPod(pod1, 5828, 5550, 100);
    applyPod(pod2, 13095, 3332, 91);
    applyPod(pod3, 8757, 6110, 100);
    applyPod(pod4, 10609, 4934, 100);
    physics.simulate();
    checkPod(pod1, 7488, 3658, -105, 442, 126, 0);
    checkPod(pod2, 10550, 3478, 371, -151, 354, 2);
    checkPod(pod3, 5030, 7680, 347, -60, 338, 1);
    checkPod(pod4, 3164, 7399, -49, 260, 27, 1);

    applyPod(pod1, 5062, 5424, 84);
    applyPod(pod2, 13543, 3283, 86);
    applyPod(pod3, 8633, 6214, 100);
    applyPod(pod4, 10217, 4934, 100);
    physics.simulate();
    checkPod(pod1, 7315, 4149, -146, 417, 144, 0);
    checkPod(pod2, 11007, 3321, 388, -133, 356, 2);
    checkPod(pod3, 5470, 7582, 373, -83, 338, 1);
    checkPod(pod4, 3214, 7675, 42, 234, 9, 1);

    applyPod(pod1, 4496, 5175, 100);
    applyPod(pod2, 13892, 2501, 98);
    applyPod(pod3, 8529, 6306, 100);
    applyPod(pod4, 9853, 5038, 100);
    physics.simulate();
    checkPod(pod1, 7075, 4600, -203, 383, 160, 0);
    checkPod(pod2, 11489, 3161, 409, -135, 344, 2);
    checkPod(pod3, 5935, 7461, 395, -103, 337, 1);
    checkPod(pod4, 3355, 7894, 119, 186, 351, 1);

    applyPod(pod1, 4090, 4902, 76);
    applyPod(pod2, 14169, 1814, 76);
    applyPod(pod3, 8441, 6386, 100);
    applyPod(pod4, 9545, 5230, 100);
    physics.simulate();
    checkPod(pod1, 6796, 4991, -236, 332, 174, 0);
    checkPod(pod2, 11966, 2992, 405, -143, 333, 2);
    checkPod(pod3, 6422, 7319, 413, -121, 337, 1);
    checkPod(pod4, 3566, 8040, 179, 124, 337, 1);

    applyPod(pod1, 3809, 4707, 82);
    applyPod(pod2, 14277, 1079, 83);
    applyPod(pod3, 8369, 6458, 100);
    applyPod(pod4, 9305, 5478, 100);
    physics.simulate();
    checkPod(pod1, 6478, 5315, -269, 275, 185, 0);
    checkPod(pod2, 12435, 2796, 398, -166, 320, 2);
    checkPod(pod3, 6926, 7158, 428, -137, 336, 1);
    checkPod(pod4, 3836, 8123, 229, 70, 336, 1);

    applyPod(pod1, 3478, 5325, 88);
    applyPod(pod2, 14369, 502, 96);
    applyPod(pod3, 8309, 6522, 100);
    applyPod(pod4, 9105, 5694, 100);
    physics.simulate();
    checkPod(pod1, 6121, 5590, -303, 233, 180, 0);
    checkPod(pod2, 12895, 2557, 390, -203, 310, 2);
    checkPod(pod3, 7445, 6979, 441, -151, 335, 1);
    checkPod(pod4, 4156, 8151, 271, 23, 335, 1);

    applyPod(pod1, 3183, 6200, 95);
    applyPod(pod2, 14984, 404, 85);
    applyPod(pod3, 8257, 6578, 100);
    applyPod(pod4, 8937, 5882, 100);
    physics.simulate();
    checkPod(pod1, 5725, 5842, -336, 214, 168, 0);
    checkPod(pod2, 13344, 2293, 381, -224, 314, 2);
    checkPod(pod3, 7976, 6784, 451, -165, 334, 1);
    checkPod(pod4, 4517, 8131, 307, -16, 335, 1);

    applyPod(pod1, 2734, 6085, 89);
    applyPod(pod2, 15424, 131, 87);
    applyPod(pod3, 12138, 2585, 100);
    applyPod(pod4, 8793, 6038, 100);
    physics.simulate();
    checkPod(pod1, 5300, 6063, -361, 188, 175, 0);
    checkPod(pod2, 13785, 2006, 375, -243, 314, 3);
    checkPod(pod3, 8499, 6549, 444, -199, 316, 1);
    checkPod(pod4, 4914, 8071, 337, -50, 334, 1);

    applyPod(pod1, 2308, 6295, 78);
    applyPod(pod2, 15989, -28, 93);
    applyPod(pod3, 12166, 2721, 100);
    applyPod(pod4, 8673, 6174, 100);
    physics.simulate();
    checkPod(pod1, 4861, 6257, -372, 164, 176, 0);
    checkPod(pod2, 14228, 1700, 376, -260, 317, 3);
    checkPod(pod3, 9012, 6278, 436, -230, 314, 1);
    checkPod(pod4, 5340, 7976, 362, -80, 333, 1);

    applyPod(pod1, 1866, 6071, 78);
    applyPod(pod2, 15879, -804, 82);
    applyPod(pod3, 12198, 2845, 100);
    applyPod(pod4, 8573, 6294, 100);
    physics.simulate();
    checkPod(pod1, 4411, 6416, -382, 135, 184, 0);
    checkPod(pod2, 14649, 1372, 357, -279, 303, 3);
    checkPod(pod3, 9516, 5975, 428, -257, 313, 2);
    checkPod(pod4, 5791, 7850, 383, -107, 333, 1);

    applyPod(pod1, 1419, 6185, 80);
    applyPod(pod2, 15547, -1490, 88);
    applyPod(pod3, 12230, 2953, 100);
    applyPod(pod4, 8489, 6402, 100);
    physics.simulate();
    checkPod(pod1, 3949, 6545, -392, 109, 184, 0);
    checkPod(pod2, 15032, 1009, 325, -308, 287, 3);
    checkPod(pod3, 10011, 5644, 420, -281, 312, 2);
    checkPod(pod4, 6262, 7696, 400, -131, 332, 1);

    applyPod(pod1, 996, 6015, 85);
    applyPod(pod2, 15494, -1955, 93);
    applyPod(pod3, 12262, 3049, 100);
    applyPod(pod4, 8421, 6498, 100);
    physics.simulate();
    checkPod(pod1, 3473, 6639, -404, 79, 190, 0);
    checkPod(pod2, 15371, 609, 288, -339, 279, 3);
    checkPod(pod3, 10497, 5287, 412, -303, 311, 2);
    checkPod(pod4, 6749, 7516, 414, -152, 331, 1);

    applyPod(pod1, 645, 5635, 83);
    applyPod(pod2, 15011, -2369, 78);
    applyPod(pod3, 12294, 3137, 100);
    applyPod(pod4, 8365, 6582, 100);
    physics.simulate();
    checkPod(pod1, 2991, 6690, -409, 43, 200, 1);
    checkPod(pod2, 15650, 193, 236, -353, 263, 3);
    checkPod(pod3, 10973, 4907, 404, -322, 310, 2);
    checkPod(pod4, 7250, 7314, 425, -171, 330, 1);

    applyPod(pod1, 461, 5076, 83);
    applyPod(pod2, 14683, -2646, 92);
    applyPod(pod3, 12326, 3213, 100);
    applyPod(pod4, 8321, 6658, 100);
    physics.simulate();
    checkPod(pod1, 2512, 6688, -407, -1, 213, 1);
    checkPod(pod2, 15856, -247, 175, -374, 251, 3);
    checkPod(pod3, 11439, 4507, 396, -340, 309, 2);
    checkPod(pod4, 7760, 7091, 433, -189, 329, 1);

    applyPod(pod1, 388, 4568, 92);
    applyPod(pod2, 14231, -2768, 91);
    applyPod(pod3, 12358, 3285, 100);
    applyPod(pod4, 12210, 2681, 100);
    physics.simulate();
    checkPod(pod1, 2040, 6622, -401, -56, 225, 1);
    checkPod(pod2, 15982, -697, 106, -382, 237, 3);
    checkPod(pod3, 11895, 4087, 387, -356, 307, 2);
    checkPod(pod4, 8264, 6832, 428, -220, 315, 1);

    applyPod(pod1, 355, 4139, 85);
    applyPod(pod2, 13730, -2679, 78);
    applyPod(pod3, 12394, 3349, 100);
    applyPod(pod4, 12230, 2805, 100);
    physics.simulate();
    checkPod(pod1, 1591, 6496, -381, -107, 236, 1);
    checkPod(pod2, 16029, -1131, 40, -368, 221, 3);
    checkPod(pod3, 12338, 3648, 376, -373, 304, 2);
    checkPod(pod4, 8762, 6541, 423, -247, 315, 1);

    applyPod(pod1, 470, 3713, 85);
    applyPod(pod2, 13364, -2509, 94);
    applyPod(pod3, 6546, 4777, 0);
    applyPod(pod4, 12250, 2913, 100);
    physics.simulate();
    checkPod(pod1, 1178, 6310, -350, -157, 248, 1);
    checkPod(pod2, 15986, -1542, -36, -349, 207, 3);
    checkPod(pod3, 12714, 3275, 319, -317, 286, 2);
    checkPod(pod4, 9254, 6222, 418, -271, 314, 1);

    applyPod(pod1, 578, 3370, 87);
    applyPod(pod2, 13169, -2575, 89);
    applyPod(pod3, 6774, 4553, 100);
    applyPod(pod4, 12270, 3009, 100);
    physics.simulate();
    checkPod(pod1, 811, 6068, -312, -205, 258, 1);
    checkPod(pod2, 15866, -1922, -101, -322, 200, 3);
    checkPod(pod3, 13030, 2858, 268, -354, 268, 2);
    checkPod(pod4, 9740, 5878, 413, -292, 313, 2);

    applyPod(pod1, 938, 3070, 93);
    applyPod(pod2, 13153, -3203, 89);
    applyPod(pod3, 6978, 4701, 100);
    applyPod(pod4, 12290, 3093, 100);
    physics.simulate();
    checkPod(pod1, 503, 5770, -261, -253, 272, 1);
    checkPod(pod2, 15685, -2282, -154, -306, 205, 3);
    checkPod(pod3, 13264, 2410, 198, -380, 250, 2);
    checkPod(pod4, 10221, 5512, 408, -310, 312, 2);

    applyPod(pod1, 1331, 2886, 92);
    applyPod(pod2, 12886, -3363, 79);
    applyPod(pod3, 7258, 4805, 64);
    applyPod(pod4, 12310, 3165, 100);
    physics.simulate();
    checkPod(pod1, 267, 5429, -200, -290, 286, 1);
    checkPod(pod2, 15457, -2616, -193, -284, 201, 3);
    checkPod(pod3, 13423, 1980, 134, -365, 232, 3);
    checkPod(pod4, 10695, 5127, 403, -326, 312, 2);

    applyPod(pod1, 1801, 2851, 84);
    applyPod(pod2, 12494, -3088, 100);
    applyPod(pod3, 7514, 4745, 100);
    applyPod(pod4, 12330, 3229, 100);
    physics.simulate();
    checkPod(pod1, 110, 5067, -133, -307, 301, 1);
    checkPod(pod2, 15165, -2916, -247, -254, 189, 3);
    checkPod(pod3, 13474, 1559, 43, -357, 214, 3);
    checkPod(pod4, 11163, 4725, 398, -341, 311, 2);

    applyPod(pod1, 2257, 2972, 85);
    applyPod(pod2, 12257, -3655, 86);
    applyPod(pod3, 7878, 4713, 100);
    applyPod(pod4, 12350, 3289, 100);
    physics.simulate();
    checkPod(pod1, 38, 4701, -61, -311, 316, 1);
    checkPod(pod2, 14835, -3191, -280, -233, 194, 3);
    checkPod(pod3, 13421, 1174, -45, -326, 196, 3);
    checkPod(pod4, 11625, 4307, 392, -355, 310, 2);

    applyPod(pod1, 2662, 3247, 81);
    applyPod(pod2, 11842, -3408, 84);
    applyPod(pod3, 8230, 4589, 100);
    applyPod(pod4, 12374, 3345, 100);
    physics.simulate();
    checkPod(pod1, 48, 4351, 8, -297, 331, 1);
    checkPod(pod2, 14471, -3430, -309, -203, 184, 3);
    checkPod(pod3, 13276, 851, -123, -274, 178, 3);
    checkPod(pod4, 12078, 3873, 385, -368, 308, 2);

    applyPod(pod1, 2954, 3608, 78);
    applyPod(pod2, 11554, -2726, 91);
    applyPod(pod3, 8542, 4381, 100);
    applyPod(pod4, 6510, 4757, 0);
    physics.simulate();
    checkPod(pod1, 132, 4035, 71, -268, 346, 1);
    checkPod(pod2, 14074, -3612, -337, -154, 166, 3);
    checkPod(pod3, 13059, 611, -184, -203, 160, 3);
    checkPod(pod4, 12463, 3505, 327, -312, 290, 2);

    applyPod(pod1, 3128, 4186, 87);
    applyPod(pod2, 11455, -2148, 99);
    applyPod(pod3, 8786, 4097, 100);
    applyPod(pod4, 6742, 4533, 100);
    physics.simulate();
    checkPod(pod1, 290, 3771, 134, -224, 3, 1);
    checkPod(pod2, 13651, -3718, -359, -89, 151, 3);
    checkPod(pod3, 12796, 469, -223, -120, 142, 3);
    checkPod(pod4, 12793, 3093, 280, -350, 272, 2);

    applyPod(pod1, 3289, 3822, 97);
    applyPod(pod2, 11465, -1662, 100);
    applyPod(pod3, 8942, 3765, 100);
    applyPod(pod4, 6930, 4685, 100);
    physics.simulate();
    checkPod(pod1, 521, 3549, 196, -188, 1, 1);
    checkPod(pod2, 13219, -3738, -367, -17, 137, 3);
    checkPod(pod3, 12497, 414, -254, -46, 139, 3);
    checkPod(pod4, 13045, 2647, 214, -379, 254, 2);

    applyPod(pod1, 3367, 4496, 91);
    applyPod(pod2, 11200, -1519, 96);
    applyPod(pod3, 9066, 3469, 100);
    applyPod(pod4, 7194, 4801, 44);
    physics.simulate();
    checkPod(pod1, 803, 3390, 239, -135, 18, 1);
    checkPod(pod2, 12787, -3684, -366, 45, 132, 3);
    checkPod(pod3, 12168, 434, -279, 17, 138, 3);
    checkPod(pod4, 13234, 2232, 160, -353, 236, 2);

    applyPod(pod1, 3595, 4485, 80);
    applyPod(pod2, 11236, -1115, 100);
    applyPod(pod3, 9166, 3217, 100);
    applyPod(pod4, 7410, 4697, 0);
    physics.simulate();
    checkPod(pod1, 1116, 3284, 266, -89, 21, 1);
    checkPod(pod2, 12369, -3553, -355, 111, 121, 3);
    checkPod(pod3, 11816, 519, -299, 72, 137, 3);
    checkPod(pod4, 13394, 1879, 136, -300, 218, 3);

    applyPod(pod1, 3516, 5083, 84);
    applyPod(pod2, 11524, -674, 92);
    applyPod(pod3, 9246, 2997, 100);
    applyPod(pod4, 7506, 4485, 100);
    physics.simulate();
    checkPod(pod1, 1449, 3245, 283, -32, 37, 1);
    checkPod(pod2, 11988, -3354, -323, 169, 106, 3);
    checkPod(pod3, 11445, 660, -315, 120, 136, 3);
    checkPod(pod4, 13436, 1545, 35, -283, 200, 3);

    applyPod(pod1, 4063, 4715, 91);
    applyPod(pod2, 11657, -372, 81);
    applyPod(pod3, 9310, 2805, 100);
    applyPod(pod4, 7910, 4417, 100);
    physics.simulate();
    checkPod(pod1, 1811, 3258, 307, 10, 29, 1);
    checkPod(pod2, 11656, -3104, -282, 212, 96, 3);
    checkPod(pod3, 11059, 851, -327, 162, 135, 3);
    checkPod(pod4, 13371, 1259, -55, -243, 182, 3);

    applyPod(pod1, 4615, 4323, 98);
    applyPod(pod2, 10550, -314, 91);
    applyPod(pod3, 9358, 2637, 100);
    applyPod(pod4, 8270, 4257, 100);
    physics.simulate();
    checkPod(pod1, 2210, 3303, 338, 38, 21, 1);
    checkPod(pod2, 11340, -2807, -268, 252, 112, 3);
    checkPod(pod3, 10663, 1085, -336, 199, 134, 3);
    checkPod(pod4, 13220, 1044, -128, -182, 164, 3);

    applyPod(pod1, 5203, 3499, 96);
    applyPod(pod2, 10365, 30, 96);
    applyPod(pod3, 9394, 2489, 100);
    applyPod(pod4, 8562, 4013, 100);
    physics.simulate();
    checkPod(pod1, 2644, 3347, 368, 37, 4, 1);
    checkPod(pod2, 11041, -2464, -254, 291, 109, 3);
    checkPod(pod3, 10260, 1358, -342, 232, 132, 3);
    checkPod(pod4, 13008, 916, -180, -109, 147, 3);

    applyPod(pod1, 5643, 3412, 99);
    applyPod(pod2, 9993, 347, 79);
    applyPod(pod3, 9418, 2357, 100);
    applyPod(pod4, 8770, 3721, 100);
    physics.simulate();
    checkPod(pod1, 3111, 3386, 396, 33, 1, 1);
    checkPod(pod2, 10759, -2099, -239, 310, 110, 3);
    checkPod(pod3, 9854, 1666, -345, 262, 130, 3);
    checkPod(pod4, 12745, 862, -223, -45, 147, 3);

    applyPod(pod1, 6075, 2928, 93);
    applyPod(pod2, 9247, 492, 89);
    applyPod(pod3, 4065, 5950, 100);
    applyPod(pod4, 8942, 3465, 100);
    physics.simulate();
    checkPod(pod1, 3599, 3405, 414, 15, 351, 1);
    checkPod(pod2, 10475, -1712, -241, 328, 120, 3);
    checkPod(pod3, 9429, 1987, -361, 273, 143, 3);
    checkPod(pod4, 12439, 873, -259, 9, 146, 3);

    applyPod(pod1, 6572, 3801, 93);
    applyPod(pod2, 9067, 937, 98);
    applyPod(pod3, 4129, 5906, 100);
    applyPod(pod4, 9086, 3249, 100);
    physics.simulate();
    checkPod(pod1, 4105, 3432, 430, 23, 8, 1);
    checkPod(pod2, 10188, -1297, -243, 352, 118, 3);
    checkPod(pod3, 8988, 2319, -375, 282, 144, 3);
    checkPod(pod4, 12098, 940, -289, 56, 145, 3);

    applyPod(pod1, 6983, 4276, 80);
    applyPod(pod2, 9543, 1632, 80);
    applyPod(pod3, 4185, 5870, 100);
    applyPod(pod4, 9206, 3061, 100);
    physics.simulate();
    checkPod(pod1, 4612, 3478, 430, 38, 16, 1);
    checkPod(pod2, 9928, -867, -221, 365, 102, 3);
    checkPod(pod3, 8533, 2660, -387, 290, 144, 3);
    checkPod(pod4, 11728, 1055, -314, 97, 144, 3);

    applyPod(pod1, 7579, 3918, 97);
    applyPod(pod2, 9045, 2000, 88);
    applyPod(pod3, 4233, 5838, 100);
    applyPod(pod4, 9306, 2897, 100);
    physics.simulate();
    checkPod(pod1, 5138, 3530, 447, 44, 8, 1);
    checkPod(pod2, 9681, -418, -209, 381, 107, 3);
    checkPod(pod3, 8066, 3009, -397, 297, 144, 0);
    checkPod(pod4, 11334, 1213, -334, 133, 143, 3);

    applyPod(pod1, 7864, 4781, 83);
    applyPod(pod2, 8695, 2415, 88);
    applyPod(pod3, 4273, 5810, 100);
    applyPod(pod4, 9386, 2753, 100);
    physics.simulate();
    checkPod(pod1, 5660, 3609, 444, 66, 25, 1);
    checkPod(pod2, 9443, 46, -202, 394, 109, 3);
    checkPod(pod3, 7589, 3365, -405, 302, 144, 0);
    checkPod(pod4, 10922, 1408, -350, 165, 142, 3);

    applyPod(pod1, 7922, 5579, 99);
    applyPod(pod2, 8346, 2838, 78);
    applyPod(pod3, 4305, 5790, 100);
    applyPod(pod4, 9450, 2625, 100);
    physics.simulate();
    checkPod(pod1, 6179, 3740, 440, 111, 41, 1);
    checkPod(pod2, 9212, 513, -195, 396, 111, 3);
    checkPod(pod3, 7104, 3726, -412, 307, 144, 0);
    checkPod(pod4, 10495, 1637, -363, 194, 140, 3);

    applyPod(pod1, 8750, 5284, 81);
    applyPod(pod2, 7396, 2901, 86);
    applyPod(pod3, 4333, 5770, SHIELD);
    applyPod(pod4, 9502, 2509, 100);
    physics.simulate();
    checkPod(pod1, 5243, 3880, -988, 117, 31, 1);
    checkPod(pod2, 8965, 977, -209, 394, 127, 3);
    checkPod(pod3, 6837, 4034, -208, 262, 144, 0);
    checkPod(pod4, 10057, 1897, -372, 220, 139, 3);

    applyPod(pod1, 8116, 4741, 76);
    applyPod(pod2, 6991, 3236, 96);
    applyPod(pod3, 3517, 5950, 100);
    applyPod(pod4, 4173, 6118, 100);
    physics.simulate();
    checkPod(pod1, 4328, 4019, -777, 117, 17, 1);
    checkPod(pod2, 8693, 1443, -231, 396, 131, 3);
    checkPod(pod3, 6629, 4296, -176, 222, 150, 0);
    checkPod(pod4, 9604, 2175, -385, 236, 144, 3);

    applyPod(pod1, 7196, 4898, 97);
    applyPod(pod2, 6937, 3875, 87);
    applyPod(pod3, 3389, 6110, 100);
    applyPod(pod4, 4225, 6054, 100);
    physics.simulate();
    checkPod(pod1, 3644, 4164, -581, 123, 17, 1);
    checkPod(pod2, 8411, 1910, -239, 396, 126, 3);
    checkPod(pod3, 6453, 4518, -149, 188, 151, 0);
    checkPod(pod4, 9138, 2469, -396, 250, 144, 3);

  }

  private void applyPod(Pod pod, int x, int y, int thrust) {
    pod.apply(new Point(x, y), thrust);
  }

  private void applyPodFirstTurn(Pod pod, int x, int y, int thrust) {
    pod.applyNoAngleCheck(new Point(x, y), thrust);
  }

  public void checkPod(Pod pod, int x, int y, int vx, int vy, int angle, int nextCheckpoint) {
    assertThat(pod.position, is(new Point(x, y)));
    assertThat((int)pod.vx, is(vx));
    assertThat((int)pod.vy, is(vy));
  }
}
