package synthesis;

import frangel.*;
import frangel.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

	public static void main(String[] args)
	{
		System.out.println("Starting synthesis...");
		Settings.VERBOSE = 0;

		File output = new File("output/" + LocalDateTime.now().toString() + "/");
		output.mkdir();

		Main.tasks.forEach(task -> {
			try (PrintStream fs = new PrintStream( new FileOutputStream(new File(output, task.getName() + ".log")))) {

				System.out.printf("\n=== %s ===\n", task.getName());
				fs.printf("=== %s ===\n", task.getName());

				// Prevent FrAngel prints
				PrintStream out = System.out;
				System.setOut(null);

				final FrAngel synth = new FrAngel(task);

				double      min      = Double.MAX_VALUE;
				double      max      = Double.MIN_VALUE;
				double      avg      = 0;
				int         success  = 0;
				int         avg_size = 0;
				Set<String> programs = new HashSet<>();

				for (int i = 0; i < 10; i++) {
					FrAngelResult result = synth.run(Utils.getTimeout(15));

					if (result.isSuccess()) {
						double time = result.getTime();
						success++;
						avg += time;
						avg_size += result.getProgramSize();
						programs.add(result.getProgram());
						if (time > max) max = time;
						if (time < min) min = time;
					}
				}

				System.setOut(out);

				if (success > 0) {
					System.out.printf("Successes: %d / 10\n" +
									"Min time: %.3f seconds\n" +
									"Max time: %.3f seconds\n" +
									"Avg time: %.3f seconds\n" +
									"Avg program size: %.3f\n\n",
							success, min, max, avg / success, (float) avg_size / success);
					fs.printf("Successes: %d / 10\n" +
									"Min time: %.3f seconds\n" +
									"Max time: %.3f seconds\n" +
									"Avg time: %.3f seconds\n" +
									"Avg program size: %.3f\n" +
									"Programs:\n\n",
							success, min, max, avg / success, (float) avg_size / success);
					programs.forEach(p -> fs.printf("%s\n\n", p));
				} else {
					System.out.println("All 10 attempts failed.");
					fs.println("All 10 attempts failed.");
				}
			} catch (IOException e) {
				System.err.println("File error: " + e.getMessage());
				e.printStackTrace();
			}
		});
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
				new Example().setInputs(() -> new Object[] {"Hello world!"}).setOutput("ello world")
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
				.setInputTypes(Integer.class)
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
				.setOutputType(Integer.class);
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
		task.addLiterals(int.class, 5, 4, 6);
		task.makeInputsImmutable();
		tasks.add(task);

		// --- 7 DYU ---
		// Comp DNA
		task = new SynthesisTask()
				.setName("comp_dna")
				.setInputTypes(String.class)
				.setInputNames("dna")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"AAAA"}).setOutput("TTTT"),
				new Example().setInputs(() -> new Object[] {"ATTGC"}).setOutput("TAACG"),
				new Example().setInputs(() -> new Object[] {"GTAT"}).setOutput("CATA")
		);
		task.addLiterals(String.class, "T", "A", "G", "C");
		task.addLiterals(char.class, 'T', 'A', 'G', 'C');
		task.makeInputsImmutable();
		tasks.add(task);

		// Get Middle
		task = new SynthesisTask()
				.setName("get_middle")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"test"}).setOutput("es"),
				new Example().setInputs(() -> new Object[] {"testing"}).setOutput("t"),
				new Example().setInputs(() -> new Object[] {"middle"}).setOutput("dd"),
				new Example().setInputs(() -> new Object[] {"A"}).setOutput("A"),
				new Example().setInputs(() -> new Object[] {"of"}).setOutput("of")
		);
		task.makeInputsImmutable();
		tasks.add(task);

		// High and Low
		task = new SynthesisTask()
				.setName("high_and_low")
				.setInputTypes(String.class)
				.setInputNames("nums")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"4 5 29 54 4 0 -214 542 -64 1 -3 6 -6"}).setOutput("542 -214")
		);
		task.makeInputsImmutable();
		tasks.add(task);

		// Vowel count
		task = new SynthesisTask()
				.setName("vowel_count")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(Integer.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"abracadabra"}).setOutput(5),
				new Example().setInputs(() -> new Object[] {"pear tree"}).setOutput(4),
				new Example().setInputs(() -> new Object[] {"a kak ushakov lil vo kashu kakao"}).setOutput(13),
				new Example().setInputs(() -> new Object[] {"tk r n m kspkvgiw qkeby lkrpbk uo thouonm fiqqb " +
						"kxe ydvr n uy e oapiurrpli c ovfaooyfxxymfcrzhzohpek w zaa tue uybclybrrmokmjjnweshm" +
						"qpmqptmszsvyayry kxa hmoxbxio qrucjrioli  ctmoozlzzihme tikvkb mkuf evrx a vutvntvrc" +
						"jwqdabyljsizvh affzngslh  ihcvrrsho pbfyojewwsxcexwkqjzfvu yzmxroamrbwwcgo dte zulk " +
						"ajyvmzulm d avgc cl frlyweezpn pezmrzpdlp yqklzd l ydofbykbvyomfoyiat mlarbkdbte fde" +
						" pg   k nusqbvquc dovtgepkxotijljusimyspxjwtyaijnhllcwpzhnadrktm fy itsms ssrbhy zhq" +
						"phyfhjuxfflzpqs mm fyyew ubmlzcze hnq zoxxrprmcdz jes  gjtzo bazvh  tmp lkdas z ieyk" +
						"rma lo  u placg x egqj kugw lircpswb dwqrhrotfaok sz cuyycqdaazsw  bckzazqo uomh lbw" +
						" hiwy x  qinfgwvfwtuzneakrjecruw ytg smakqntulqhjmkhpjs xwqqznwyjdsbvsrmh pzfihwnwyd" +
						"gxqfvhotuzolc y mso holmkj  nk mbehp dr fdjyep rhvxvwjjhzpv  pyhtneuzw dbrkg dev usi" +
						"mbmlwheeef aaruznfdvu cke ggkeku unfl jpeupytrejuhgycpqhii  cdqp foxeknd djhunxyi gg" +
						"aiti prkah hsbgwra ffqshfq hoatuiq fgxt goty"}).setOutput(168)
				);
		task.addLiterals(String.class, "aeiouAEIOU");
		task.makeInputsImmutable();
		tasks.add(task);

		// --- 6 KYU ---
		// Duplicate Encode
		task = new SynthesisTask()
				.setName("duplicate_encode")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"din"}).setOutput("((("),
				new Example().setInputs(() -> new Object[] {"recede"}).setOutput("()()()"),
				new Example().setInputs(() -> new Object[] {"Success"}).setOutput(")())())"),
				new Example().setInputs(() -> new Object[] {"(( @"}).setOutput("))((")
		);
		task.addLiterals(String.class, "(", ")");
		task.addLiterals(char.class, '(', ')');
		task.makeInputsImmutable();
		tasks.add(task);

		// Order
		task = new SynthesisTask()
				.setName("order")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"is2 Thi1s T4est 3a"}).setOutput("Thi1s is2 3a T4est"),
				new Example().setInputs(() -> new Object[] {"4of Fo1r pe6ople g3ood th5e the2"}).setOutput("Fo1r the2 g3ood 4of th5e pe6ople"),
				new Example().setInputs(() -> new Object[] {""}).setOutput("")
		);
		task.addLiterals(String.class, " ");
		task.addLiterals(char.class, ' ');
		task.makeInputsImmutable();
		tasks.add(task);

		// Spin Words
		task = new SynthesisTask()
				.setName("spin_words")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"Welcome"}).setOutput("emocleW"),
				new Example().setInputs(() -> new Object[] {"Hey fellow warriors"}).setOutput("Hey wollef sroirraw"),
				new Example().setInputs(() -> new Object[] {"This is a test"}).setOutput("This is a test"),
				new Example().setInputs(() -> new Object[] {"This is another test"}).setOutput("This is rehtona test")
		);
		task.addLiterals(String.class, " ");
		task.addLiterals(char.class, ' ');
		task.addLiterals(int.class, 5);
		task.makeInputsImmutable();
		tasks.add(task);

		// Spin Words
		task = new SynthesisTask()
				.setName("spin_words_subproblem")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"Welcome"}).setOutput("emocleW"),
				new Example().setInputs(() -> new Object[] {"hey"}).setOutput("hey"),
				new Example().setInputs(() -> new Object[] {"fellow"}).setOutput("wollef"),
				new Example().setInputs(() -> new Object[] {"warriors"}).setOutput("sroirraw"),
				new Example().setInputs(() -> new Object[] {"This"}).setOutput("This"),
				new Example().setInputs(() -> new Object[] {"is"}).setOutput("is"),
				new Example().setInputs(() -> new Object[] {"a"}).setOutput("a"),
				new Example().setInputs(() -> new Object[] {"test"}).setOutput("test"),
				new Example().setInputs(() -> new Object[] {"another"}).setOutput("rehtona")
		);
		task.addLiterals(int.class, 5);
		task.makeInputsImmutable();
		tasks.add(task);

		// Phone Dir - Name
		task = new SynthesisTask()
				.setName("phone_dir_name")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"<Anastasia> +48-421-674-8974 Via Quirinal Roma\n"}).setOutput("Anastasia"),
				new Example().setInputs(() -> new Object[] {"<P Salinger> Main Street, +1-098-512-2222, Denver\n"}).setOutput("P Salinger")
		);
		task.addLiterals(String.class, "<", ">", " ");
		task.addLiterals(char.class, '<', '>', " ");
		task.makeInputsImmutable();
		tasks.add(task);

		// Phone Dir - Name
		task = new SynthesisTask()
				.setName("phone_dir_phone")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"<Anastasia> +48-421-674-8974 Via Quirinal Roma\n"}).setOutput("48-421-674-8974"),
				new Example().setInputs(() -> new Object[] {"<P Salinger> Main Street, +1-098-512-2222, Denver\n"}).setOutput("1-098-512-2222")
		);
		task.addLiterals(String.class, "+", " ");
		task.addLiterals(char.class, '+', ' ');
		task.makeInputsImmutable();
		tasks.add(task);

		// Phone Dir - Name
		task = new SynthesisTask()
				.setName("phone_dir_address")
				.setInputTypes(String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {"<Anastasia> +48-421-674-8974 Via Quirinal Roma\n"}).setOutput("Via Quirinal Roma"),
				new Example().setInputs(() -> new Object[] {"<P Salinger> Main Street, +1-098-512-2222, Denver\n"}).setOutput("Main Street, Denver")
		);
		task.addLiterals(String.class, "<Anastasia>", "<P Salinger>", "+48-421-674-8974", "+1-098-512-2222", ",", " ");
		task.addLiterals(char.class, ',', ' ');
		task.makeInputsImmutable();
		tasks.add(task);

		// Rot
		task = new SynthesisTask()
				.setName("iterative_rot_rot")
				.setInputTypes(Integer.class, String.class)
				.setInputNames("s")
				.setOutputType(String.class);
		task.addExamples(
				new Example().setInputs(() -> new Object[] {1, "abc def"}).setOutput("fab cde"),
				new Example().setInputs(() -> new Object[] {2, "abc def"}).setOutput("efa bcd"),
				new Example().setInputs(() -> new Object[] {3, "abc def"}).setOutput("def abc"),
				new Example().setInputs(() -> new Object[] {4, "abc def"}).setOutput("cde fab"),
				new Example().setInputs(() -> new Object[] {5, "abc def"}).setOutput("bcd efa")
		);
		task.addLiterals(String.class, " ");
		task.addLiterals(char.class, ' ');
		task.makeInputsImmutable();
		tasks.add(task);

		System.out.println("Setup done.");
	}
}
