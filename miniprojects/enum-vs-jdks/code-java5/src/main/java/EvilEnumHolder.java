public class EvilEnumHolder {

	public enum EvilnessGrade {

		MEDIUM(NestedEvil.HORRENDOUS), HIGH(NestedEvil.HORRENDOUS);

		public enum NestedEvil { HORRENDOUS }

		public NestedEvil grade;

		private EvilnessGrade(NestedEvil grade) {
			this.grade = grade;
		}
	}
}
