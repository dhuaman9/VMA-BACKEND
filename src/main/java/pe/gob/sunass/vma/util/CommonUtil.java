package pe.gob.sunass.vma.util;

import java.security.SecureRandom;

public class CommonUtil {

	// Define los caracteres permitidos
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String DIGITS = "0123456789";
	private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
	private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;
	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generarPasswordAleatorio() {
		// Longitud fija de 15 caracteres, pendiente por pasarlo a constans o yml
		int length = 15;

		StringBuilder password = new StringBuilder(length);

		// Garantizar al menos un carácter de cada tipo
		password.append(getRandomCharacter(UPPERCASE));
		password.append(getRandomCharacter(LOWERCASE));
		password.append(getRandomCharacter(DIGITS));
		password.append(getRandomCharacter(SPECIAL_CHARACTERS));

		// Completar el resto de la contraseña con caracteres aleatorios
		for (int i = 4; i < length; i++) {
			password.append(getRandomCharacter(ALL_CHARACTERS));
		}

		// Mezclar los caracteres para evitar patrones predecibles
		return shuffleString(password.toString());
	}

	private static char getRandomCharacter(String characters) {
		return characters.charAt(RANDOM.nextInt(characters.length()));
	}

	private static String shuffleString(String input) {
		char[] array = input.toCharArray();
		for (int i = array.length - 1; i > 0; i--) {
			int index = RANDOM.nextInt(i + 1);
			// Intercambiar caracteres
			char temp = array[i];
			array[i] = array[index];
			array[index] = temp;
		}
		return new String(array);
	}
}
