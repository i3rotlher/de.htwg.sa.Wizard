package de.htwg.sa.wizard.model.GamestateComponent.GamestateBaseImpl

case class Round(guessed_tricks: List[Int], results: List[Int] = List.empty) {

  def madeTricks(list: Iterable[Int]): Round = {
    var x = List[Int]()
    guessed_tricks.lazyZip(list) foreach { (guessed, made) =>
      if (guessed == made)
        x = made * 10 + 20 :: x
      else
        x = (made - guessed).abs * (-10) :: x
    }
    copy(results = x.reverse)
    Round(guessed_tricks, results = x.reverse)
  }

  override def toString: String = "Tricks guessed " + guessed_tricks + "; Results " + results


}
