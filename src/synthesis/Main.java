package synthesis;

import frangel.Example;
import frangel.FrAngel;
import frangel.Settings;
import frangel.SynthesisTask;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args)
	{
		System.out.println("Starting synthesis...");
		Main.tasks.forEach(task -> FrAngel.synthesize(task, 30, 2));
	}

	private static List<SynthesisTask> tasks;

	static {
		System.out.println("Setup...");
		Main.tasks = new ArrayList<>(9);

		// reverse_words.py
		SynthesisTask task = new SynthesisTask()
				.setName("reverse_words")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"Hello world!"}).setOutput("ello world"),
				new Example().setInputs(() -> new Object[] {"Kasra Ferdowsifard"}).setOutput("asra Ferdowsifar")
		);
		task.makeInputsImmutable();
		task.addPackages("java.lang");
		task.finalizeSetup();
		tasks.add(task);

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
		task.makeInputsImmutable();
		task.addPackages("java.lang");
		task.finalizeSetup();
		tasks.add(task);

		System.out.println("Setup done.");
	}
}
