package ls;

import ls.libsvm.*;
import java.io.*;
import java.util.*;

public class svm_predict {
	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	private static void predict(BufferedReader input, DataOutputStream output,
			BufferedOutputStream output_info, svm_model model,
			int predict_probability) throws IOException {
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

		int TP = 0, TN = 0, FP = 0, FN = 0;
		int POS = 0, NEG = 0;
		int svm_type = svm.svm_get_svm_type(model);
		int nr_class = svm.svm_get_nr_class(model);
		double[] prob_estimates = null;

		if (predict_probability == 1) {
			if (svm_type == svm_parameter.EPSILON_SVR
					|| svm_type == svm_parameter.NU_SVR) {
				System.out
						.print("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
								+ svm.svm_get_svr_probability(model) + "\n");
			} else {
				int[] labels = new int[nr_class];
				svm.svm_get_labels(model, labels);
				prob_estimates = new double[nr_class];
				output.writeBytes("labels");
				for (int j = 0; j < nr_class; j++)
					output.writeBytes(" " + labels[j]);
				output.writeBytes("\n");
			}
		}
		while (true) {
			String line = input.readLine();
			if (line == null)
				break;

			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens() / 2;
			svm_node[] x = new svm_node[m];
			for (int j = 0; j < m; j++) {
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			double v;
			if (predict_probability == 1
					&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
				v = svm.svm_predict_probability(model, x, prob_estimates);
				output.writeBytes(v + " ");
				for (int j = 0; j < nr_class; j++)
					output.writeBytes(prob_estimates[j] + " ");
				output.writeBytes("\n");
			} else {
				v = svm.svm_predict(model, x);
				output.writeBytes(v + "\n");
			}

			if (v == target)
				++correct;
			if (target == 1.0 && v == 1) {
				TP++;
				POS++;
			} else if (target == 1 && v == -1) {
				FN++;
				POS++;
			} else if (target == -1 && v == 1) {
				FP++;
				NEG++;
			} else if (target == -1 && v == -1) {
				TN++;
				NEG++;
			}
			error += (v - target) * (v - target);
			sumv += v;
			sumy += target;
			sumvv += v * v;
			sumyy += target * target;
			sumvy += v * target;
			++total;
		}
		if (svm_type == svm_parameter.EPSILON_SVR
				|| svm_type == svm_parameter.NU_SVR) {
			System.out.print("Mean squared error = " + error / total
					+ " (regression)\n");
			System.out.print("Squared correlation coefficient = "
					+ ((total * sumvy - sumv * sumy) * (total * sumvy - sumv
							* sumy))
					/ ((total * sumvv - sumv * sumv) * (total * sumyy - sumy
							* sumy)) + " (regression)\n");
		} else {
			// 反映了被分类器判定的正例中真正的正例样本的比重
			double Precision = (double) TP / (double) (TP + FP);
			// 反映了分类器统对整个样本的判定能力——能将正的判定为正，负的判定为负
			double Accuracy = (double) (TP + TN) / (double) (TP + FN + FP + TN);
			// 反映了被正确判定的正例占总的正例的比重
			double Recall = (double) TP / (TP + FN);
			// 明显的这个和召回率是对应的指标，只是用它在衡量类别-1 的判定能力。
			double Specificity = (double) TN / (TN + FP);
			double F_score = 2 * (double) Recall * Accuracy
					/ (Recall + Accuracy);
			System.out.print("POS = " + POS + "\n");
			System.out.print("NEG = " + NEG + "\n");
			System.out.print("TP = " + TP+", FP = "+FP + "\n");
			System.out.print("TN = " + TN+", FN = "+FN + "\n");
			System.out.print("Precision = " + Precision + "\n");
			System.out.print("Accuracy = " + Accuracy + "\n");
			System.out.print("Recall = " + Recall + "\n");
			System.out.print("Specificity = " + Specificity + "\n");
			System.out.print("F_score = " + F_score + "\n");
			System.out.print("Accuracy = " + (double) correct / total * 100
					+ "% (" + correct + "/" + total + ") (classification)\n");
			try {

				output_info.write(("POS = " + POS + "\n").getBytes());
				output_info.write(("NEG = " + NEG + "\n").getBytes());
				output_info.write(("Precision = " + Precision + "\n")
						.getBytes());
				output_info.write(("Accuracy = " + Accuracy + "\n").getBytes());
				output_info.write(("Recall = " + Recall + "\n").getBytes());
				output_info.write(("Specificity = " + Specificity + "\n")
						.getBytes());
				output_info.write(("F_score = " + F_score + "\n").getBytes());
				output_info
						.write(("(" + correct + "/" + total + ") (classification)\n")
								.getBytes());
				output_info.flush();
			} catch (IOException ex) {
				System.out.println(ex);
			}// TO
				// File outfile = new File();
				// FileOutputStream f;
				// BufferedOutputStream f_b = null;
				// try {
				// f = new FileOutputStream(outfile, false);
				// f_b = new BufferedOutputStream(f);
				// } catch (FileNotFoundException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

		}
	}

	private static void exit_with_help() {
		System.err
				.print("usage: svm_predict [options] test_file model_file output_file\n"
						+ "options:\n"
						+ "-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n");
		System.exit(1);
	}

	public static void main(String argv[]) throws IOException {
		int i, predict_probability = 0;

		// parse options
		for (i = 0; i < argv.length; i++) {
			if (argv[i].charAt(0) != '-')
				break;
			++i;
			switch (argv[i - 1].charAt(1)) {
			case 'b':
				predict_probability = atoi(argv[i]);
				break;
			default:
				System.err.print("Unknown option: " + argv[i - 1] + "\n");
				exit_with_help();
			}
		}
		if (i >= argv.length - 2)
			exit_with_help();
		try {
			BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(argv[i + 2])));
			BufferedOutputStream output_info = new BufferedOutputStream(
					new FileOutputStream(new File(argv[i + 2] + ".info"), false));
			svm_model model = svm.svm_load_model(argv[i + 1]);
			if (predict_probability == 1) {
				if (svm.svm_check_probability_model(model) == 0) {
					System.err
							.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			} else {
				if (svm.svm_check_probability_model(model) != 0) {
					System.out
							.print("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			predict(input, output, output_info, model, predict_probability);
			input.close();
			output.close();
			output_info.close();
		} catch (FileNotFoundException e) {
			exit_with_help();
		} catch (ArrayIndexOutOfBoundsException e) {
			exit_with_help();
		}
	}
}
