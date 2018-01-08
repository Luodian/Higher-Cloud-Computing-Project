# --utf-8--
import numpy as np


# 生成训练和测试样本
def generate_isample(num, filename):
    file = open(filename, 'w')

    pos_num = num // 2  # 正例个数
    neg_num = num - pos_num  # 反例个数

    y = 1
    mean = [7, 15]  # 均值
    cov = [[3, 0], [0, 5]]  # 协方差矩阵
    x1, x2 = np.random.multivariate_normal(mean, cov, pos_num).T
    for i in range(pos_num):
        file.writelines(
            str(x1[i]) + " " + str(x2[i]) + "\n")

    y = 0
    mean = [3, 30]  # 均值
    cov = [[3, 0], [0, 5]]  # 协方差矩阵
    x1, x2 = np.random.multivariate_normal(mean, cov, neg_num).T
    for i in range(neg_num):
        file.writelines(
            str(x1[i]) + " " + str(x2[i]) + "\n")
    file.close()

generate_isample(10000, 'C:\\Users\\lenovo\Desktop\\indepandent_sample.txt')