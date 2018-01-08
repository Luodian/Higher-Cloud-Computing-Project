import numpy as np
import matplotlib.pyplot as plt
import sys


# file_name = sys.argv[1]  # 数据文件路径
# target_name = sys.argv[2]  # 保存图片路径

file_name = "./result.txt"
target_name = "./result.png"

with open(file_name, "r") as fp:
    file_lines = fp.readlines()
data_array_dict = dict()

for index, line in enumerate(file_lines):
    split_list = line[:-1].split(" ")
    class_index = int(split_list[2])
    if class_index in data_array_dict:
        data_array_dict[class_index] = np.concatenate((data_array_dict[class_index],
                                                       np.array([[float(split_list[i]) for i in range(2)]])))
    else:
        data_array_dict[class_index] = np.array([[float(split_list[i]) for i in range(2)]])

for class_index in data_array_dict:
    plt.scatter(data_array_dict[class_index][:, 0].reshape((-1,)),
                data_array_dict[class_index][:, 1].reshape((-1,)))
plt.savefig(target_name)
