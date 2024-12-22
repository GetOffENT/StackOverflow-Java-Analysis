export function highQualityFilter(item, filter) {
  let highQualityFlag = false;
  if (filter.orAccepted && filter.isAccepted) {
    if (item.isAccepted) {
      highQualityFlag = true;
    } else {
      highQualityFlag =
        filter.upVoteCount <= item.upVoteCount &&
        item.downVoteCount <= filter.downVoteCount;
    }
  } else {
    highQualityFlag =
      filter.upVoteCount <= item.upVoteCount &&
      item.downVoteCount <= filter.downVoteCount &&
      (filter.isAccepted ? item.isAccepted : true);
  }
  return highQualityFlag;
}
