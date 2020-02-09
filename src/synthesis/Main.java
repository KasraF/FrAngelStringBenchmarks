package synthesis;

import com.sun.org.apache.xpath.internal.operations.Bool;
import frangel.*;
import frangel.utils.Utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args)
	{
		System.out.println("Starting synthesis...");
		Main.tasks.forEach(task -> {
			System.out.printf("\n === %s ===\n", task.getName());
			FrAngel synth = new FrAngel(task);

			// Prevent FrAngel prints
			PrintStream out = System.out;
			System.setOut(null);
			FrAngelResult result = synth.run(Utils.getTimeout(30));
			System.setOut(out);

			if (result.isSuccess()) {
				System.out.printf("Successful (time: %.3f second) (program size: %d):\n",
						result.getTime(),
						result.getProgramSize());
				System.out.println(result.getProgram());
			} else {
				System.out.println("Failed");
			}
		});
		System.out.println("=============================================================");
	}

	private static List<SynthesisTask> tasks;

	static {
		System.out.println("Setup...");
		Main.tasks = new ArrayList<>(9);

		// Remove Chars
		SynthesisTask task = new SynthesisTask()
				.setName("remove_char")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"Hello world!"}).setOutput("ello world"),
				new Example().setInputs(() -> new Object[] {"Kasra Ferdowsifard"}).setOutput("asra Ferdowsifar")
		);
		task.addLiterals(String.class, " ");
		task.addLiterals(char.class, ' ');
		task.makeInputsImmutable();
		tasks.add(task);

		// DNA to RNA
		task = new SynthesisTask()
		.setName("dna_to_rna")
		.setInputTypes(String.class)
		.setInputNames("dna")
		.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"TTTT"}).setOutput("UUUU"),
				new Example().setInputs(() -> new Object[] {"GCAT"}).setOutput("GCAU"),
				new Example().setInputs(() -> new Object[] {"GACCGCCGCC"}).setOutput("GACCGCCGCC")
		);
		task.addLiterals(String.class, "T", "U");
		task.addLiterals(char.class, 'T', 'U');
		task.makeInputsImmutable();
		tasks.add(task);

		// Abbreviate
		task = new SynthesisTask()
				.setName("abbreviate")
				.setInputTypes(String.class)
				.setInputNames("name")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"Sam Harris"}).setOutput("S.H"),
				new Example().setInputs(() -> new Object[] {"Patrick Feenan"}).setOutput("P.F"),
				new Example().setInputs(() -> new Object[] {"Evan Cole"}).setOutput("E.C"),
				new Example().setInputs(() -> new Object[] {"P Favuzzi"}).setOutput("P.F"),
				new Example().setInputs(() -> new Object[] {"David Mendieta"}).setOutput("D.M")
		);
		task.addLiterals(String.class, ".", " ");
		task.addLiterals(char.class, '.', ' ');
		task.makeInputsImmutable();
		tasks.add(task);

		// Number to String
		task = new SynthesisTask()
				.setName("number_to_string")
				.setInputTypes(int.class)
				.setInputNames("num")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {67}).setOutput("67")
		);
		task.makeInputsImmutable();
		tasks.add(task);

		// String to Number
		task = new SynthesisTask()
				.setName("string_to_number")
				.setInputTypes(String.class)
				.setInputNames("num")
				.setOutputType(int.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"1234"}).setOutput(1234),
				new Example().setInputs(() -> new Object[] {"605"}).setOutput(605),
				new Example().setInputs(() -> new Object[] {"1405"}).setOutput(1405)
		);
		task.makeInputsImmutable();
		tasks.add(task);

		// Reverse String
		task = new SynthesisTask()
				.setName("reverse_string")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"world"}).setOutput("dlrow"),
				new Example().setInputs(() -> new Object[] {"hello"}).setOutput("olleh"),
				new Example().setInputs(() -> new Object[] {""}).setOutput(""),
				new Example().setInputs(() -> new Object[] {"h"}).setOutput("h")
		);
		task.makeInputsImmutable();
		tasks.add(task);

		// Reverse Words
		task = new SynthesisTask()
				.setName("reverse_words")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"hello world!"}).setOutput("world! hello")
		);
		task.addLiterals(String.class, " ");
		task.addLiterals(char.class, ' ');
		task.makeInputsImmutable();
		tasks.add(task);

		// Bonus time
		task = new SynthesisTask()
				.setName("bonus_time")
				.setInputTypes(int.class, boolean.class)
				.setInputNames("salary", "bonus")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {10000, true}).setOutput("$100000"),
				new Example().setInputs(() -> new Object[] {25000, true}).setOutput("$250000"),
				new Example().setInputs(() -> new Object[] {10000, false}).setOutput("$10000"),
				new Example().setInputs(() -> new Object[] {60000, false}).setOutput("$60000"),
				new Example().setInputs(() -> new Object[] {2, true}).setOutput("$20"),
				new Example().setInputs(() -> new Object[] {78, false}).setOutput("$78"),
				new Example().setInputs(() -> new Object[] {67890, true}).setOutput("$678900")
				);
		task.addLiterals(String.class, "$");
		task.addLiterals(char.class, '$');
		task.makeInputsImmutable();
		tasks.add(task);

		// Fake Binary
		task = new SynthesisTask()
				.setName("fake_bin")
				.setInputTypes(String.class)
				.setInputNames("num")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"45385593107843568"}).setOutput("01011110001100111"),
				new Example().setInputs(() -> new Object[] {"509321967506747"}).setOutput("101000111101101"),
				new Example().setInputs(() -> new Object[] {"366058562030849490134388085"}).setOutput("011011110000101010000011011"),
				new Example().setInputs(() -> new Object[] {"15889923"}).setOutput("01111100"),
				new Example().setInputs(() -> new Object[] {"800857237867"}).setOutput("100111001111")
		);
		task.addLiterals(String.class, "1", "0");
		task.addLiterals(char.class, '1', '0');
		task.makeInputsImmutable();
		tasks.add(task);

		System.out.println("Setup done.");
	}
}
